package com.kj.steady.data.model

data class UserPreferences(
    val userName: String = "KJ",
    val startWeight: Double = 104.0,
    val goalWeight: Double = 85.0,
    val startDate: String = "2026-11-04",
    val deadlineDate: String = "2027-02-01",
    val heightCm: Double = 175.5,
    val age: Int = 35,
    
    // Nutrition & Hydration Targets (Editable)
    val dailyCalorieTarget: Int = 2050,
    val dailyProteinTargetGrams: Double = 140.0,
    val dailyHydrationGoalMl: Int = 2800,
    val dailyStepGoal: Int = 11000,
    val dailySleepGoalHours: Double = 7.5,

    // Personalized Profile Context
    val homeEquipment: String = "Yoga mat, 10kg resistance band, 2 x 3kg dumbbells",
    val dietaryFocus: String = "Eggitarian (Paneer & Eggs focus)",
    val bpStatusNote: String = "Pending first resting measurement",
    val kidneyStoneStatusNote: String = "No issues for a long time; 2.8L hydration maintenance",
    val medicationsNote: String = "None at present",
    
    // Routine Schedules (HH:mm)
    val weekdayWakeTime: String = "05:45",
    val weekendWakeTime: String = "07:00",
    val morningWalkTime: String = "06:30",
    val breakfastTime: String = "08:15",
    val officeStartTime: String = "09:30",
    val officeEndTime: String = "18:30",
    val dinnerPrepTime: String = "19:15",
    val eveningLogTime: String = "21:00",
    val bedtime: String = "22:15",
    
    // Quiet Hours
    val quietHoursStart: String = "22:30",
    val quietHoursEnd: String = "05:45",
    
    // Notification toggles
    val notifyWeighIn: Boolean = true,
    val notifyHydration: Boolean = true,
    val notifyActivity: Boolean = true,
    val notifyDinnerPrep: Boolean = true,
    val notifyEveningLog: Boolean = true,
    val notifyWeeklyReview: Boolean = true,
    
    // Health Connect
    val isHealthConnectEnabled: Boolean = false,
    val preferredStepsSource: String = "Samsung Health", // "Samsung Health", "Fitbit", "Auto (Highest Source)"
    val preferredSleepSource: String = "Auto Merge", // "Auto Merge", "Samsung Health", "Fitbit"
    val lastSyncTimestamp: Long = 0L,
    
    // Adaptive Learning
    val isAdaptiveLearningEnabled: Boolean = true,
    val autoAdjustReminders: Boolean = false, // strictly requires approval by default
    
    // Optional AI
    val isAiEnabled: Boolean = false,
    val aiMonthlySpendingLimitUsd: Double = 1.0,
    val aiSpentThisMonthUsd: Double = 0.0,
    val customAiEndpoint: String = "",
    val customAiApiKey: String = "",

    // In-App Self-Updater
    val updateCheckUrl: String = "https://raw.githubusercontent.com/lycanthropekj/kj-steady/main/version.json"
)
