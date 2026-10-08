package com.aaspaas.customer.core.database.dao

import androidx.room.*
import com.aaspaas.customer.core.database.entity.SearchHistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchHistoryDao {

    @Query("SELECT * FROM search_history ORDER BY timestamp DESC")
    fun getAllSearchHistory(): Flow<List<SearchHistoryEntity>>

    @Query("SELECT * FROM search_history WHERE query LIKE :query ORDER BY timestamp DESC LIMIT :limit")
    fun searchByQuery(query: String, limit: Int): Flow<List<SearchHistoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSearchHistory(history: SearchHistoryEntity)

    @Query("DELETE FROM search_history WHERE timestamp < :cutoff")
    suspend fun deleteOldHistory(cutoff: Long)

    @Query("DELETE FROM search_history")
    suspend fun clearAllHistory()
}
