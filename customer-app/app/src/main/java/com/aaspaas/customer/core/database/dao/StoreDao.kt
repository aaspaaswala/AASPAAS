package com.aaspaas.customer.core.database.dao

import androidx.room.*
import com.aaspaas.customer.core.database.entity.StoreEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface StoreDao {

    @Query("SELECT * FROM stores ORDER BY distanceKm ASC")
    fun getAllStores(): Flow<List<StoreEntity>>

    @Query("SELECT * FROM stores WHERE latitude BETWEEN :minLat AND :maxLat AND longitude BETWEEN :minLng AND :maxLng ORDER BY distanceKm ASC")
    fun getNearbyStores(
        minLat: Double, maxLat: Double,
        minLng: Double, maxLng: Double
    ): Flow<List<StoreEntity>>

    @Query("SELECT * FROM stores WHERE id = :id")
    fun getStoreById(id: String): Flow<StoreEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStore(store: StoreEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStores(stores: List<StoreEntity>)

    @Query("DELETE FROM stores")
    suspend fun deleteAllStores()
}
