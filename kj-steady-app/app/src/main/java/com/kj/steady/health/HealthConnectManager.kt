package com.kj.steady.health

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.*
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import com.kj.steady.data.model.UserPreferences
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.*

enum class HealthConnectStatus {
    AVAILABLE,
    UPDATE_REQUIRED,
    UNAVAILABLE
}

data class HealthSyncResult(
    val steps: Int? = null,
    val sleepHours: Double? = null,
    val weightKg: Double? = null,
    val sourceName: String = "Health Connect",
    val stepPackage: String? = null,
    val sleepPackage: String? = null,
    val recordsFound: Boolean = false
)

data class TimeInterval(val start: Instant, val end: Instant)

class HealthConnectManager(private val context: Context) {

    val requiredPermissions: Set<String> = setOf(
        HealthPermission.getReadPermission(StepsRecord::class),
        HealthPermission.getReadPermission(SleepSessionRecord::class),
        HealthPermission.getReadPermission(WeightRecord::class),
        HealthPermission.getReadPermission(HeartRateRecord::class),
        HealthPermission.getReadPermission(ExerciseSessionRecord::class)
    )

    val corePermissions: Set<String> = setOf(
        HealthPermission.getReadPermission(StepsRecord::class),
        HealthPermission.getReadPermission(SleepSessionRecord::class),
        HealthPermission.getReadPermission(WeightRecord::class)
    )

    fun checkAvailability(): HealthConnectStatus {
        return try {
            when (HealthConnectClient.getSdkStatus(context)) {
                HealthConnectClient.SDK_AVAILABLE -> HealthConnectStatus.AVAILABLE
                HealthConnectClient.SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED -> HealthConnectStatus.UPDATE_REQUIRED
                else -> HealthConnectStatus.UNAVAILABLE
            }
        } catch (e: Exception) {
            HealthConnectStatus.UNAVAILABLE
        }
    }

    fun getClient(): HealthConnectClient? {
        return try {
            if (checkAvailability() == HealthConnectStatus.AVAILABLE) {
                HealthConnectClient.getOrCreate(context)
            } else null
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getGrantedPermissions(): Set<String> {
        val client = getClient() ?: return emptySet()
        return try {
            client.permissionController.getGrantedPermissions()
        } catch (e: Exception) {
            emptySet()
        }
    }

    suspend fun hasCorePermissions(): Boolean {
        val granted = getGrantedPermissions()
        return granted.containsAll(corePermissions)
    }

    suspend fun hasAnyPermission(): Boolean {
        val granted = getGrantedPermissions()
        return granted.any { it in requiredPermissions }
    }

    fun openHealthConnectSettings(ctx: Context) {
        val intents = listOf(
            Intent("android.health.connect.action.HEALTH_HOME_SETTINGS"),
            Intent("androidx.health.ACTION_HEALTH_CONNECT_SETTINGS"),
            Intent(Settings.ACTION_SETTINGS)
        )
        for (intent in intents) {
            try {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                ctx.startActivity(intent)
                return
            } catch (e: Exception) {
                // Continue to next fallback
            }
        }
    }

    fun openHealthConnectPlayStore(ctx: Context) {
        val intents = listOf(
            Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.google.android.apps.healthdata&url=healthconnect%3A%2F%2Fonboarding")),
            Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.healthdata"))
        )
        for (intent in intents) {
            try {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                ctx.startActivity(intent)
                return
            } catch (e: Exception) {
                // Continue to next fallback
            }
        }
    }

    suspend fun fetchDailyMetrics(dateStr: String, userPrefs: UserPreferences): HealthSyncResult? {
        val client = getClient() ?: return null
        val granted = getGrantedPermissions()
        if (granted.isEmpty()) return null

        try {
            val zone = ZoneId.of("Asia/Kolkata")
            val localDate = LocalDate.parse(dateStr)
            val startOfDay = localDate.atStartOfDay(zone).toInstant()
            val endOfDay = localDate.plusDays(1).atStartOfDay(zone).toInstant()

            // 1. Fetch Steps (if permission granted)
            var chosenStepPackage: String? = null
            var totalSteps: Int? = null
            if (granted.contains(HealthPermission.getReadPermission(StepsRecord::class))) {
                val stepsRequest = ReadRecordsRequest(
                    recordType = StepsRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(startOfDay, endOfDay)
                )
                val stepsResponse = client.readRecords(stepsRequest)
                if (stepsResponse.records.isNotEmpty()) {
                    val recordsByPackage = stepsResponse.records.groupBy { it.metadata.dataOrigin.packageName }
                    val packageTotals = recordsByPackage.mapValues { entry ->
                        entry.value.sumOf { it.count.toInt() }
                    }

                    val samsungPkg = "com.sec.android.app.shealth"
                    val fitbitPkg = "com.fitbit.FitbitMobile"
                    val googleFitPkg = "com.google.android.apps.fitness"

                    totalSteps = when {
                        userPrefs.preferredStepsSource.contains("Samsung", ignoreCase = true) && packageTotals.containsKey(samsungPkg) -> {
                            chosenStepPackage = "Samsung Health"
                            packageTotals[samsungPkg]
                        }
                        (userPrefs.preferredStepsSource.contains("Fitbit", ignoreCase = true) || userPrefs.preferredStepsSource.contains("Google", ignoreCase = true)) &&
                                (packageTotals.containsKey(fitbitPkg) || packageTotals.containsKey(googleFitPkg)) -> {
                            val pkg = if (packageTotals.containsKey(fitbitPkg)) fitbitPkg else googleFitPkg
                            chosenStepPackage = if (pkg == fitbitPkg) "Fitbit" else "Google Fit"
                            packageTotals[pkg]
                        }
                        else -> {
                            val maxEntry = packageTotals.maxByOrNull { it.value }
                            if (maxEntry != null) {
                                chosenStepPackage = friendlyPackageName(maxEntry.key)
                                maxEntry.value
                            } else null
                        }
                    }
                }
            }

            // 2. Fetch Sleep (if permission granted)
            var chosenSleepPackage: String? = null
            var sleepHours: Double? = null
            if (granted.contains(HealthPermission.getReadPermission(SleepSessionRecord::class))) {
                val sleepStart = localDate.minusDays(1).atTime(18, 0).atZone(zone).toInstant()
                val sleepEnd = localDate.atTime(18, 0).atZone(zone).toInstant()
                val sleepRequest = ReadRecordsRequest(
                    recordType = SleepSessionRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(sleepStart, sleepEnd)
                )
                val sleepResponse = client.readRecords(sleepRequest)
                if (sleepResponse.records.isNotEmpty()) {
                    val recordsByPackage = sleepResponse.records.groupBy { it.metadata.dataOrigin.packageName }
                    val samsungPkg = "com.sec.android.app.shealth"
                    val fitbitPkg = "com.fitbit.FitbitMobile"

                    val relevantRecords = when {
                        userPrefs.preferredSleepSource.contains("Samsung", ignoreCase = true) && recordsByPackage.containsKey(samsungPkg) -> {
                            chosenSleepPackage = "Samsung Health"
                            recordsByPackage[samsungPkg] ?: emptyList()
                        }
                        userPrefs.preferredSleepSource.contains("Fitbit", ignoreCase = true) && recordsByPackage.containsKey(fitbitPkg) -> {
                            chosenSleepPackage = "Fitbit"
                            recordsByPackage[fitbitPkg] ?: emptyList()
                        }
                        else -> {
                            chosenSleepPackage = "Merged Sources"
                            sleepResponse.records
                        }
                    }

                    val intervals = relevantRecords.map { TimeInterval(it.startTime, it.endTime) }
                    val merged = mergeIntervals(intervals)
                    val totalSeconds = merged.sumOf { Duration.between(it.start, it.end).seconds }
                    sleepHours = String.format(Locale.US, "%.1f", totalSeconds / 3600.0).toDoubleOrNull()
                }
            }

            // 3. Fetch Weight (if permission granted)
            var weightKg: Double? = null
            if (granted.contains(HealthPermission.getReadPermission(WeightRecord::class))) {
                val weightRequest = ReadRecordsRequest(
                    recordType = WeightRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(startOfDay, endOfDay)
                )
                val weightResponse = client.readRecords(weightRequest)
                val morningStart = localDate.atTime(5, 0).atZone(zone).toInstant()
                val morningEnd = localDate.atTime(11, 0).atZone(zone).toInstant()
                val weightRecord = weightResponse.records.find {
                    it.time.isAfter(morningStart) && it.time.isBefore(morningEnd)
                } ?: weightResponse.records.firstOrNull()
                weightKg = weightRecord?.weight?.inKilograms
            }

            val recordsFound = totalSteps != null || sleepHours != null || weightKg != null
            val sourceParts = listOfNotNull(
                chosenStepPackage?.let { "Steps: $it" },
                chosenSleepPackage?.let { "Sleep: $it" }
            )
            val sourceLabel = if (sourceParts.isNotEmpty()) {
                "Health Connect (${sourceParts.joinToString(", ")})"
            } else "Health Connect Sync"

            return HealthSyncResult(
                steps = totalSteps,
                sleepHours = sleepHours,
                weightKg = weightKg,
                sourceName = sourceLabel,
                stepPackage = chosenStepPackage,
                sleepPackage = chosenSleepPackage,
                recordsFound = recordsFound
            )
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    private fun friendlyPackageName(pkg: String): String {
        return when {
            pkg.contains("shealth", ignoreCase = true) -> "Samsung Health"
            pkg.contains("fitbit", ignoreCase = true) -> "Fitbit"
            pkg.contains("fitness", ignoreCase = true) -> "Google Fit"
            else -> pkg.substringAfterLast('.')
        }
    }

    companion object {
        fun mergeIntervals(intervals: List<TimeInterval>): List<TimeInterval> {
            if (intervals.isEmpty()) return emptyList()
            val sorted = intervals.sortedBy { it.start }
            val merged = mutableListOf<TimeInterval>()
            var current = sorted[0]
            for (i in 1 until sorted.size) {
                val next = sorted[i]
                if (!next.start.isAfter(current.end)) {
                    current = TimeInterval(
                        start = current.start,
                        end = if (next.end.isAfter(current.end)) next.end else current.end
                    )
                } else {
                    merged.add(current)
                    current = next
                }
            }
            merged.add(current)
            return merged
        }
    }
}
