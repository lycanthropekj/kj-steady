package com.kj.steady

import android.app.Application
import com.kj.steady.data.AppDatabase
import com.kj.steady.data.repository.SteadyRepository
import com.kj.steady.health.HealthConnectManager
import com.kj.steady.logic.AdaptiveEngine
import com.kj.steady.logic.V1MigrationHelper
import com.kj.steady.notifications.AlarmScheduler
import com.kj.steady.notifications.NotificationHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class KJSteadyApp : Application() {

    val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    val database by lazy { AppDatabase.getDatabase(this, applicationScope) }
    val repository by lazy { SteadyRepository(this, database) }
    val healthConnectManager by lazy { HealthConnectManager(this) }

    override fun onCreate() {
        super.onCreate()

        // 1. Setup notification channels
        NotificationHelper.createNotificationChannels(this)

        // 2. Perform background checks: v1 migration, reminder scheduling, adaptive rules
        applicationScope.launch(Dispatchers.IO) {
            // Check legacy prototype SharedPreferences and migrate if present
            V1MigrationHelper.checkAndMigrateLegacySharedPreferences(
                this@KJSteadyApp,
                database.dailyEntryDao(),
                database.bloodPressureDao()
            )

            // Setup routine schedule
            val prefs = repository.getUserPreferences()
            AlarmScheduler.scheduleAllReminders(this@KJSteadyApp, prefs)

            // Evaluate patterns for adaptive suggestions
            if (prefs.isAdaptiveLearningEnabled) {
                AdaptiveEngine.evaluatePatterns(
                    database.dailyEntryDao(),
                    database.notificationDao(),
                    database.adaptiveDao()
                )
            }
        }
    }
}
