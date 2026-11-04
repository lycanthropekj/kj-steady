package com.kj.steady.logic

import com.kj.steady.data.dao.AdaptiveDao
import com.kj.steady.data.dao.DailyEntryDao
import com.kj.steady.data.dao.NotificationDao
import com.kj.steady.data.model.AdaptiveSuggestion

object AdaptiveEngine {

    suspend fun evaluatePatterns(
        dailyEntryDao: DailyEntryDao,
        notificationDao: NotificationDao,
        adaptiveDao: AdaptiveDao
    ) {
        val entries = dailyEntryDao.getAllEntries()
        val recentEntries = entries.take(7)

        // 1. Low sleep & recovery check
        val recentSleep = recentEntries.mapNotNull { it.sleepHours }
        if (recentSleep.isNotEmpty() && recentSleep.average() < 6.0) {
            adaptiveDao.upsertSuggestion(
                AdaptiveSuggestion(
                    id = "low_sleep_recovery_pacing",
                    title = "Recovery-First Activity Pacing",
                    rationale = "Your recent sleep averaged %.1f hours (under recommended 7h). High-impact jogging with restricted sleep elevates injury risk and cortisol.".format(recentSleep.average()),
                    proposedAction = "Focus on comfortable 8,000–10,000 brisk walking steps and postpone interval jogging until rest improves.",
                    category = "RECOVERY"
                )
            )
        }

        // 2. High steps adherence recognition
        val recentSteps = recentEntries.mapNotNull { it.totalSteps }
        if (recentSteps.isNotEmpty() && recentSteps.average() >= 10000) {
            adaptiveDao.upsertSuggestion(
                AdaptiveSuggestion(
                    id = "step_base_established",
                    title = "Walking Base Well Established",
                    rationale = "You averaged %,.0f daily steps over the past week. Consistent walking provides reliable cardiovascular base without joint overload.".format(recentSteps.average()),
                    proposedAction = "Keep this 11k baseline steady as your anchor habit.",
                    category = "ROUTINE"
                )
            )
        }

        // 3. Evening Dinner Takeout friction reduction based on EXPLICIT user recording
        val recordedDinners = recentEntries.filter { it.isDinnerHomeCooked != null || !it.dinnerText.isNullOrBlank() }
        val takeoutCount = recordedDinners.count { entry ->
            when {
                entry.isDinnerHomeCooked != null -> entry.isDinnerHomeCooked == false
                else -> {
                    val text = entry.dinnerText?.lowercase() ?: ""
                    text.contains("order") || text.contains("swiggy") || text.contains("zomato") || text.contains("takeout") || text.contains("restaurant")
                }
            }
        }
        if (recordedDinners.size >= 4 && takeoutCount >= 3) {
            adaptiveDao.upsertSuggestion(
                AdaptiveSuggestion(
                    id = "dinner_friction_helper",
                    title = "Dinner Preparation Reminder",
                    rationale = "$takeoutCount of your last ${recordedDinners.size} recorded dinners were ordered takeout. Restaurant meals carry higher hidden sodium and cooking fats, affecting fluid retention and BP.",
                    proposedAction = "Set an early 7:15 PM dinner reminder with 15-minute quick Egg Bhurji or Paneer presets.",
                    category = "NUTRITION"
                )
            )
        }
    }
}
