package com.kj.steady.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notification_logs")
data class NotificationLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestampMillis: Long = System.currentTimeMillis(),
    val category: String, // WEIGH_IN, HYDRATION, ACTIVITY, EVENING_LOG, DINNER_PREP
    val title: String,
    val message: String,
    val actionTaken: String = "DELIVERED" // DELIVERED, SNOOZED, COMPLETED, DISMISSED
)

@Entity(tableName = "adaptive_suggestions")
data class AdaptiveSuggestion(
    @PrimaryKey
    val id: String, // e.g. "snooze_weigh_in", "low_sleep_activity"
    val title: String,
    val rationale: String,
    val proposedAction: String,
    val category: String, // ROUTINE, NUTRITION, HYDRATION, RECOVERY
    val status: String = "PENDING", // PENDING, APPROVED, DISMISSED
    val createdAt: Long = System.currentTimeMillis()
)
