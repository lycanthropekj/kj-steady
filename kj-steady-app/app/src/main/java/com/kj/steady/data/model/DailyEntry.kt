package com.kj.steady.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_entries")
data class DailyEntry(
    @PrimaryKey
    val date: String, // YYYY-MM-DD
    val morningWeight: Double? = null,
    val totalSteps: Int? = null,
    val sleepHours: Double? = null,
    val sleepQuality: Int? = null, // 1 to 5
    val cigarettes: Int? = null,
    val hadAlcohol: Boolean = false,
    val alcoholUnits: Double? = null,
    val hungerRating: Int? = null, // 1 to 5
    val energyRating: Int? = null, // 1 to 5
    val recoveryRating: Int? = null, // 1 to 5
    val breakfastText: String? = null,
    val lunchSnacksText: String? = null,
    val dinnerText: String? = null,
    val isDinnerHomeCooked: Boolean? = null, // true = home cooked, false = takeaway/ordered, null = unset
    val notes: String? = null,
    val exerciseMinutes: Int? = null,
    val exerciseNotes: String? = null,
    val sourceAttribution: String = "Manual",
    val isVerifiedMorningWeight: Boolean = false,
    val updatedAt: Long = System.currentTimeMillis()
)
