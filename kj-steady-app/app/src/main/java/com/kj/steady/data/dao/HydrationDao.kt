package com.kj.steady.data.dao

import androidx.room.*
import com.kj.steady.data.model.HydrationLog
import kotlinx.coroutines.flow.Flow

@Dao
interface HydrationDao {
    @Query("SELECT * FROM hydration_logs WHERE date = :date ORDER BY timestampMillis DESC")
    fun getLogsForDateFlow(date: String): Flow<List<HydrationLog>>

    @Query("SELECT * FROM hydration_logs WHERE date = :date ORDER BY timestampMillis DESC")
    suspend fun getLogsForDate(date: String): List<HydrationLog>

    @Query("SELECT COALESCE(SUM(amountMl), 0) FROM hydration_logs WHERE date = :date")
    fun getTotalMlForDateFlow(date: String): Flow<Int>

    @Query("SELECT COALESCE(SUM(amountMl), 0) FROM hydration_logs WHERE date = :date")
    suspend fun getTotalMlForDate(date: String): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: HydrationLog): Long

    @Delete
    suspend fun deleteLog(log: HydrationLog)

    @Query("DELETE FROM hydration_logs WHERE id = (SELECT id FROM hydration_logs WHERE date = :date ORDER BY timestampMillis DESC LIMIT 1)")
    suspend fun deleteLatestForDate(date: String): Int

    @Query("DELETE FROM hydration_logs")
    suspend fun clearAll()
}
