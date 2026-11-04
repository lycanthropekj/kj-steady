package com.kj.steady.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "hydration_logs")
data class HydrationLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: String, // YYYY-MM-DD
    val timestampMillis: Long = System.currentTimeMillis(),
    val amountMl: Int,
    val beverageType: String = "WATER", // WATER, ELECTROLYTES, TEA_COFFEE, OTHER
    val vesselLabel: String = "Glass 250ml"
)
