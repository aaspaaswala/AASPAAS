package com.aaspaas.customer.data.repository

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.aaspaas.customer.domain.model.Product
import com.aaspaas.customer.domain.model.Store
import com.aaspaas.customer.domain.repository.ProductRepository
import com.aaspaas.customer.domain.repository.StoreRepository
import com.aaspaas.customer.domain.repository.WishlistRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.wishlistDataStore by preferencesDataStore("wishlist_prefs")

@Singleton
class WishlistRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context,
    private val productRepository: ProductRepository,
    private val storeRepository: StoreRepository
) : WishlistRepository {

    private val productIdsKey = stringSetPreferencesKey("wishlist_product_ids")
    private val storeIdsKey = stringSetPreferencesKey("wishlist_store_ids")

    override val wishlistedProductIds: Flow<Set<String>> =
        context.wishlistDataStore.data.map { it[productIdsKey] ?: emptySet() }

    override val wishlistedStoreIds: Flow<Set<String>> =
        context.wishlistDataStore.data.map { it[storeIdsKey] ?: emptySet() }

    override suspend fun addToWishlist(productId: String) {
        context.wishlistDataStore.edit { prefs ->
            val current = prefs[productIdsKey] ?: emptySet()
            prefs[productIdsKey] = current + productId
        }
    }

    override suspend fun removeFromWishlist(productId: String) {
        context.wishlistDataStore.edit { prefs ->
            val current = prefs[productIdsKey] ?: emptySet()
            prefs[productIdsKey] = current - productId
        }
    }

    override suspend fun addStoreToWishlist(storeId: String) {
        context.wishlistDataStore.edit { prefs ->
            val current = prefs[storeIdsKey] ?: emptySet()
            prefs[storeIdsKey] = current + storeId
        }
    }

    override suspend fun removeStoreFromWishlist(storeId: String) {
        context.wishlistDataStore.edit { prefs ->
            val current = prefs[storeIdsKey] ?: emptySet()
            prefs[storeIdsKey] = current - storeId
        }
    }

    override suspend fun getWishlistedProducts(): Result<List<Product>> {
        val ids = wishlistedProductIds.first()
        if (ids.isEmpty()) return Result.success(emptyList())
        return ids.mapNotNull { id ->
            productRepository.getProductById(id).getOrNull()
        }.let { Result.success(it) }
    }

    override suspend fun getWishlistedStores(): Result<List<Store>> {
        val ids = wishlistedStoreIds.first()
        if (ids.isEmpty()) return Result.success(emptyList())
        return ids.mapNotNull { id ->
            storeRepository.getStoreById(id).getOrNull()
        }.let { Result.success(it) }
    }

    override fun isProductWishlisted(productId: String): Flow<Boolean> =
        wishlistedProductIds.map { productId in it }

    override fun isStoreWishlisted(storeId: String): Flow<Boolean> =
        wishlistedStoreIds.map { storeId in it }

    override suspend fun clearAll() {
        context.wishlistDataStore.edit { it.clear() }
    }
}
