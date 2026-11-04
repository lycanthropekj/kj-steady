package com.kj.steady.data.dao

import androidx.room.*
import com.kj.steady.data.model.BloodPressureReading
import kotlinx.coroutines.flow.Flow

@Dao
interface BloodPressureDao {
    @Query("SELECT * FROM blood_pressure_readings WHERE date = :date ORDER BY timestampMillis ASC")
    fun getReadingsForDateFlow(date: String): Flow<List<BloodPressureReading>>

    @Query("SELECT * FROM blood_pressure_readings WHERE date = :date ORDER BY timestampMillis ASC")
    suspend fun getReadingsForDate(date: String): List<BloodPressureReading>

    @Query("SELECT * FROM blood_pressure_readings ORDER BY timestampMillis DESC")
    fun getAllReadingsFlow(): Flow<List<BloodPressureReading>>

    @Query("SELECT * FROM blood_pressure_readings ORDER BY timestampMillis DESC")
    suspend fun getAllReadings(): List<BloodPressureReading>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReading(reading: BloodPressureReading): Long

    @Delete
    suspend fun deleteReading(reading: BloodPressureReading)

    @Query("DELETE FROM blood_pressure_readings WHERE date = :date")
    suspend fun deleteReadingsForDate(date: String)

    @Query("DELETE FROM blood_pressure_readings")
    suspend fun clearAll()
}
