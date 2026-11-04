package com.kj.steady.data.dao

import androidx.room.*
import com.kj.steady.data.model.DailyEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyEntryDao {
    @Query("SELECT * FROM daily_entries ORDER BY date DESC")
    fun getAllEntriesFlow(): Flow<List<DailyEntry>>

    @Query("SELECT * FROM daily_entries ORDER BY date DESC")
    suspend fun getAllEntries(): List<DailyEntry>

    @Query("SELECT * FROM daily_entries WHERE date = :date LIMIT 1")
    suspend fun getEntryByDate(date: String): DailyEntry?

    @Query("SELECT * FROM daily_entries WHERE date = :date LIMIT 1")
    fun getEntryByDateFlow(date: String): Flow<DailyEntry?>

    @Query("SELECT * FROM daily_entries WHERE morningWeight IS NOT NULL ORDER BY date ASC")
    fun getWeightEntriesAscFlow(): Flow<List<DailyEntry>>

    @Query("SELECT * FROM daily_entries WHERE morningWeight IS NOT NULL ORDER BY date ASC")
    suspend fun getWeightEntriesAsc(): List<DailyEntry>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertEntry(entry: DailyEntry)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertEntries(entries: List<DailyEntry>)

    @Query("DELETE FROM daily_entries WHERE date = :date")
    suspend fun deleteEntryByDate(date: String)

    @Query("DELETE FROM daily_entries")
    suspend fun clearAll()
}
