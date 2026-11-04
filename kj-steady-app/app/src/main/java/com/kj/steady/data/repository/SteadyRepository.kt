package com.kj.steady.data.repository

import android.content.Context
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonObject
import com.kj.steady.data.AppDatabase
import com.kj.steady.data.model.*
import com.kj.steady.logic.DateUtils
import com.kj.steady.logic.V1MigrationHelper
import kotlinx.coroutines.flow.Flow
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class SteadyRepository(
    private val context: Context,
    private val database: AppDatabase
) {
    private val prefs = context.getSharedPreferences("kj_steady_prefs", Context.MODE_PRIVATE)
    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()

    // Preferences
    fun getUserPreferences(): UserPreferences {
        return UserPreferences(
            userName = prefs.getString("user_name", "KJ") ?: "KJ",
            startWeight = prefs.getFloat("start_weight", 104.0f).toDouble(),
            goalWeight = prefs.getFloat("goal_weight", 85.0f).toDouble(),
            startDate = prefs.getString("start_date", "2026-11-04") ?: "2026-11-04",
            deadlineDate = prefs.getString("deadline_date", "2027-02-01") ?: "2027-02-01",
            dailyCalorieTarget = prefs.getInt("daily_calories", 2050),
            dailyProteinTargetGrams = prefs.getFloat("daily_protein", 140.0f).toDouble(),
            dailyHydrationGoalMl = prefs.getInt("daily_hydration", 2800),
            dailyStepGoal = prefs.getInt("daily_steps", 11000),
            dailySleepGoalHours = prefs.getFloat("daily_sleep", 7.5f).toDouble(),
            homeEquipment = prefs.getString("home_equipment", "Yoga mat, 10kg resistance band, 2 x 3kg dumbbells") ?: "Yoga mat, 10kg resistance band, 2 x 3kg dumbbells",
            dietaryFocus = prefs.getString("dietary_focus", "Eggitarian (Paneer & Eggs focus)") ?: "Eggitarian (Paneer & Eggs focus)",
            bpStatusNote = prefs.getString("bp_status_note", "Pending first resting measurement") ?: "Pending first resting measurement",
            kidneyStoneStatusNote = prefs.getString("kidney_stone_status", "No issues for a long time; 2.8L hydration maintenance") ?: "No issues for a long time; 2.8L hydration maintenance",
            medicationsNote = prefs.getString("medications_note", "None at present") ?: "None at present",
            isHealthConnectEnabled = prefs.getBoolean("hc_enabled", false),
            preferredStepsSource = prefs.getString("preferred_steps_source", "Samsung Health") ?: "Samsung Health",
            lastSyncTimestamp = prefs.getLong("last_sync_timestamp", 0L),
            isAdaptiveLearningEnabled = prefs.getBoolean("adaptive_enabled", true),
            updateCheckUrl = prefs.getString("update_check_url", "https://raw.githubusercontent.com/lycanthropekj/kj-steady/main/version.json") ?: "https://raw.githubusercontent.com/lycanthropekj/kj-steady/main/version.json"
        )
    }

    fun saveUserPreferences(p: UserPreferences) {
        prefs.edit().apply {
            putString("user_name", p.userName)
            putFloat("start_weight", p.startWeight.toFloat())
            putFloat("goal_weight", p.goalWeight.toFloat())
            putString("start_date", p.startDate)
            putString("deadline_date", p.deadlineDate)
            putInt("daily_calories", p.dailyCalorieTarget)
            putFloat("daily_protein", p.dailyProteinTargetGrams.toFloat())
            putInt("daily_hydration", p.dailyHydrationGoalMl)
            putInt("daily_steps", p.dailyStepGoal)
            putFloat("daily_sleep", p.dailySleepGoalHours.toFloat())
            putString("home_equipment", p.homeEquipment)
            putString("dietary_focus", p.dietaryFocus)
            putString("bp_status_note", p.bpStatusNote)
            putString("kidney_stone_status", p.kidneyStoneStatusNote)
            putString("medications_note", p.medicationsNote)
            putBoolean("hc_enabled", p.isHealthConnectEnabled)
            putString("preferred_steps_source", p.preferredStepsSource)
            putLong("last_sync_timestamp", p.lastSyncTimestamp)
            putBoolean("adaptive_enabled", p.isAdaptiveLearningEnabled)
            putString("update_check_url", p.updateCheckUrl)
            apply()
        }
    }

    // Daily entries
    fun getAllDailyEntriesFlow(): Flow<List<DailyEntry>> = database.dailyEntryDao().getAllEntriesFlow()
    fun getDailyEntryByDateFlow(date: String): Flow<DailyEntry?> = database.dailyEntryDao().getEntryByDateFlow(date)
    suspend fun getDailyEntryByDate(date: String): DailyEntry? = database.dailyEntryDao().getEntryByDate(date)
    suspend fun upsertDailyEntry(entry: DailyEntry) = database.dailyEntryDao().upsertEntry(entry)
    suspend fun deleteDailyEntry(date: String) = database.dailyEntryDao().deleteEntryByDate(date)

    // Blood Pressure
    fun getBpReadingsForDateFlow(date: String): Flow<List<BloodPressureReading>> = database.bloodPressureDao().getReadingsForDateFlow(date)
    suspend fun insertBpReading(reading: BloodPressureReading): Long = database.bloodPressureDao().insertReading(reading)
    suspend fun deleteBpReading(reading: BloodPressureReading) = database.bloodPressureDao().deleteReading(reading)

    // Hydration
    fun getHydrationLogsForDateFlow(date: String): Flow<List<HydrationLog>> = database.hydrationDao().getLogsForDateFlow(date)
    fun getHydrationTotalForDateFlow(date: String): Flow<Int> = database.hydrationDao().getTotalMlForDateFlow(date)
    suspend fun insertHydrationLog(log: HydrationLog): Long = database.hydrationDao().insertLog(log)
    suspend fun undoLatestHydration(date: String): Boolean = database.hydrationDao().deleteLatestForDate(date) > 0

    // Meal templates & weekly plan
    fun getAllMealTemplatesFlow(): Flow<List<MealTemplate>> = database.mealDao().getAllTemplatesFlow()
    fun getWeeklyMealPlanFlow(): Flow<List<WeeklyMealPlan>> = database.mealDao().getWeeklyPlanFlow()
    suspend fun upsertDayPlan(plan: WeeklyMealPlan) = database.mealDao().upsertDayPlan(plan)
    suspend fun insertMealTemplate(template: MealTemplate) = database.mealDao().insertTemplate(template)
    suspend fun deleteMealTemplate(template: MealTemplate) = database.mealDao().deleteTemplate(template)

    // Notifications & Adaptive
    fun getRecentNotificationLogsFlow(): Flow<List<NotificationLog>> = database.notificationDao().getRecentLogsFlow()
    suspend fun insertNotificationLog(log: NotificationLog) = database.notificationDao().insertLog(log)
    fun getAdaptiveSuggestionsFlow(): Flow<List<AdaptiveSuggestion>> = database.adaptiveDao().getAllSuggestionsFlow()
    suspend fun updateAdaptiveStatus(id: String, status: String) = database.adaptiveDao().updateStatus(id, status)
    suspend fun clearAdaptiveSuggestions() = database.adaptiveDao().clearAll()

    // Backup & Export
    suspend fun exportJsonBackup(): String {
        val prefsObj = getUserPreferences()
        val entries = database.dailyEntryDao().getAllEntries()
        val bpReadings = database.bloodPressureDao().getAllReadings()
        val today = DateUtils.today()
        val hydration = database.hydrationDao().getLogsForDate(today)

        val root = JsonObject().apply {
            addProperty("format", "KJ-Steady-Backup")
            addProperty("version", 2)
            addProperty("exportedAt", SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US).apply {
                timeZone = TimeZone.getTimeZone("UTC")
            }.format(Date()))
            add("settings", gson.toJsonTree(prefsObj))
            add("entries", gson.toJsonTree(entries))
            add("bloodPressureReadings", gson.toJsonTree(bpReadings))
            add("hydrationLogs", gson.toJsonTree(hydration))
        }
        return gson.toJson(root)
    }

    suspend fun importJsonBackup(rawJson: String): Int {
        val root = gson.fromJson(rawJson, JsonObject::class.java) ?: throw IllegalArgumentException("Invalid JSON")
        val version = if (root.has("version")) root.get("version").asInt else 1

        return if (version == 1) {
            // Prototype V1 format migration
            V1MigrationHelper.parseAndImportV1Json(rawJson, database.dailyEntryDao(), database.bloodPressureDao())
        } else {
            // Version 2 full backup
            var importedCount = 0
            if (root.has("entries")) {
                val entriesArray = root.getAsJsonArray("entries")
                val entries = mutableListOf<DailyEntry>()
                for (i in 0 until entriesArray.size()) {
                    val entry = gson.fromJson(entriesArray.get(i), DailyEntry::class.java)
                    entries.add(entry)
                }
                database.dailyEntryDao().upsertEntries(entries)
                importedCount = entries.size
            }
            if (root.has("bloodPressureReadings")) {
                val bpArray = root.getAsJsonArray("bloodPressureReadings")
                for (i in 0 until bpArray.size()) {
                    val bp = gson.fromJson(bpArray.get(i), BloodPressureReading::class.java)
                    database.bloodPressureDao().insertReading(bp)
                }
            }
            importedCount
        }
    }

    suspend fun exportCsvSummary(): String {
        val entries = database.dailyEntryDao().getAllEntries().sortedBy { it.date }
        val sb = StringBuilder()
        sb.append("Date,MorningWeightKg,Steps,SleepHours,Cigarettes,HadAlcohol,Breakfast,LunchSnacks,Dinner,Notes\n")
        for (e in entries) {
            sb.append(e.date).append(",")
            sb.append(e.morningWeight ?: "").append(",")
            sb.append(e.totalSteps ?: "").append(",")
            sb.append(e.sleepHours ?: "").append(",")
            sb.append(e.cigarettes ?: "").append(",")
            sb.append(if (e.hadAlcohol) "Yes" else "No").append(",")
            sb.append("\"").append(e.breakfastText?.replace("\"", "\"\"") ?: "").append("\",")
            sb.append("\"").append(e.lunchSnacksText?.replace("\"", "\"\"") ?: "").append("\",")
            sb.append("\"").append(e.dinnerText?.replace("\"", "\"\"") ?: "").append("\",")
            sb.append("\"").append(e.notes?.replace("\"", "\"\"") ?: "").append("\"\n")
        }
        return sb.toString()
    }
}
