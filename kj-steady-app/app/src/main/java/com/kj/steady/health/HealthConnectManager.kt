package com.kj.steady.health

import android.content.Context
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.*
import androidx.health.connect.client.request.AggregateGroupByDurationRequest
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import com.kj.steady.data.model.DailyEntry
import com.kj.steady.logic.DateUtils
import java.text.SimpleDateFormat
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
    val sourceName: String = "Health Connect"
)

class HealthConnectManager(private val context: Context) {

    val requiredPermissions = setOf(
        HealthPermission.getReadPermission(StepsRecord::class),
        HealthPermission.getReadPermission(SleepSessionRecord::class),
        HealthPermission.getReadPermission(WeightRecord::class),
        HealthPermission.getReadPermission(HeartRateRecord::class),
        HealthPermission.getReadPermission(ExerciseSessionRecord::class)
    )

    fun checkAvailability(): HealthConnectStatus {
        return when (HealthConnectClient.getSdkStatus(context)) {
            HealthConnectClient.SDK_AVAILABLE -> HealthConnectStatus.AVAILABLE
            HealthConnectClient.SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED -> HealthConnectStatus.UPDATE_REQUIRED
            else -> HealthConnectStatus.UNAVAILABLE
        }
    }

    private fun getClient(): HealthConnectClient? {
        return if (checkAvailability() == HealthConnectStatus.AVAILABLE) {
            HealthConnectClient.getOrCreate(context)
        } else null
    }

    suspend fun hasAllPermissions(): Boolean {
        val client = getClient() ?: return false
        val granted = client.permissionController.getGrantedPermissions()
        return granted.containsAll(requiredPermissions)
    }

    suspend fun fetchDailyMetrics(dateStr: String): HealthSyncResult? {
        val client = getClient() ?: return null
        if (!hasAllPermissions()) return null

        try {
            val zone = ZoneId.of("Asia/Kolkata")
            val localDate = LocalDate.parse(dateStr)
            val startOfDay = localDate.atStartOfDay(zone).toInstant()
            val endOfDay = localDate.plusDays(1).atStartOfDay(zone).toInstant()

            // 1. Fetch Steps
            val stepsRequest = ReadRecordsRequest(
                recordType = StepsRecord::class,
                timeRangeFilter = TimeRangeFilter.between(startOfDay, endOfDay)
            )
            val stepsResponse = client.readRecords(stepsRequest)
            val totalSteps = if (stepsResponse.records.isNotEmpty()) {
                stepsResponse.records.sumOf { it.count.toInt() }
            } else null

            // 2. Fetch Sleep (from 18:00 prior day to 18:00 current day)
            val sleepStart = localDate.minusDays(1).atTime(18, 0).atZone(zone).toInstant()
            val sleepEnd = localDate.atTime(18, 0).atZone(zone).toInstant()
            val sleepRequest = ReadRecordsRequest(
                recordType = SleepSessionRecord::class,
                timeRangeFilter = TimeRangeFilter.between(sleepStart, sleepEnd)
            )
            val sleepResponse = client.readRecords(sleepRequest)
            val sleepHours = if (sleepResponse.records.isNotEmpty()) {
                val totalSeconds = sleepResponse.records.sumOf {
                    Duration.between(it.startTime, it.endTime).seconds
                }
                String.format(Locale.US, "%.1f", totalSeconds / 3600.0).toDoubleOrNull()
            } else null

            // 3. Fetch Weight (morning readings prioritized)
            val weightRequest = ReadRecordsRequest(
                recordType = WeightRecord::class,
                timeRangeFilter = TimeRangeFilter.between(startOfDay, endOfDay)
            )
            val weightResponse = client.readRecords(weightRequest)
            val weightKg = weightResponse.records.firstOrNull()?.weight?.inKilograms

            return HealthSyncResult(
                steps = totalSteps,
                sleepHours = sleepHours,
                weightKg = weightKg,
                sourceName = "Health Connect Sync"
            )
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }
}
