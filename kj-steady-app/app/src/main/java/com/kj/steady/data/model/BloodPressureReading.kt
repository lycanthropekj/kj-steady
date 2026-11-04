package com.kj.steady.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "blood_pressure_readings")
data class BloodPressureReading(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val date: String, // YYYY-MM-DD
    val timestampMillis: Long = System.currentTimeMillis(),
    val systolic: Int, // mmHg
    val diastolic: Int, // mmHg
    val pulse: Int? = null, // bpm
    val arm: String = "Left", // Left or Right
    val posture: String = "Sitting", // Sitting, Lying, Standing
    val notes: String? = null
)
