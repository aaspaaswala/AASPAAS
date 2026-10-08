package com.aaspaas.customer.domain.repository

import com.aaspaas.customer.domain.model.Product
import com.aaspaas.customer.domain.model.Store
import kotlinx.coroutines.flow.Flow

interface WishlistRepository {
    val wishlistedProductIds: Flow<Set<String>>
    val wishlistedStoreIds: Flow<Set<String>>
    suspend fun addToWishlist(productId: String)
    suspend fun removeFromWishlist(productId: String)
    suspend fun addStoreToWishlist(storeId: String)
    suspend fun removeStoreFromWishlist(storeId: String)
    suspend fun getWishlistedProducts(): Result<List<Product>>
    suspend fun getWishlistedStores(): Result<List<Store>>
    fun isProductWishlisted(productId: String): Flow<Boolean>
    fun isStoreWishlisted(storeId: String): Flow<Boolean>
    suspend fun clearAll()
}
