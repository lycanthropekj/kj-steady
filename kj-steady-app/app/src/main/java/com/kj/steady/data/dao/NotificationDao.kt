package com.kj.steady.data.dao

import androidx.room.*
import com.kj.steady.data.model.NotificationLog
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notification_logs ORDER BY timestampMillis DESC LIMIT 100")
    fun getRecentLogsFlow(): Flow<List<NotificationLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: NotificationLog): Long

    @Query("UPDATE notification_logs SET actionTaken = :action WHERE id = :id")
    suspend fun updateAction(id: Long, action: String)

    @Query("SELECT COUNT(*) FROM notification_logs WHERE timestampMillis >= :startOfDayMillis")
    suspend fun getTodayNotificationCount(startOfDayMillis: Long): Int

    @Query("DELETE FROM notification_logs")
    suspend fun clearAll()
}
