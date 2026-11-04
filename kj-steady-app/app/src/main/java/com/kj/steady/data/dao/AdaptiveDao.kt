package com.kj.steady.data.dao

import androidx.room.*
import com.kj.steady.data.model.AdaptiveSuggestion
import kotlinx.coroutines.flow.Flow

@Dao
interface AdaptiveDao {
    @Query("SELECT * FROM adaptive_suggestions ORDER BY createdAt DESC")
    fun getAllSuggestionsFlow(): Flow<List<AdaptiveSuggestion>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertSuggestion(suggestion: AdaptiveSuggestion)

    @Query("UPDATE adaptive_suggestions SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: String, status: String)

    @Query("DELETE FROM adaptive_suggestions")
    suspend fun clearAll()
}
