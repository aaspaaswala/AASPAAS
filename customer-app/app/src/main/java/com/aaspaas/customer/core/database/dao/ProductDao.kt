package com.aaspaas.customer.core.database.dao

import androidx.room.*
import com.aaspaas.customer.core.database.entity.ProductEntity
import com.aaspaas.customer.core.database.entity.StoreEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    @Query("SELECT * FROM products ORDER BY name ASC")
    fun getAllProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE id = :id")
    fun getProductById(id: String): Flow<ProductEntity?>

    @Query("SELECT * FROM products WHERE storeId = :storeId")
    fun getProductsByStore(storeId: String): Flow<List<ProductEntity>>

    @Query("SELECT * FROM stores WHERE id = :storeId")
    fun getStoreById(storeId: String): Flow<StoreEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<ProductEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStore(store: StoreEntity)

    @Query("UPDATE products SET storeId = :storeId, storeName = :storeName, storeDistanceKm = :storeDistanceKm, storeRating = :storeRating, storeIsOpen = :storeIsOpen WHERE id = :id")
    suspend fun updateProductStore(
        id: String,
        storeId: String,
        storeName: String,
        storeDistanceKm: Double?,
        storeRating: Float?,
        storeIsOpen: Boolean
    )

    @Query("DELETE FROM products WHERE id = :id")
    suspend fun deleteProduct(id: String)
}
