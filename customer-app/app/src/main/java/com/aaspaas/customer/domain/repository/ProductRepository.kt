package com.aaspaas.customer.domain.repository

import com.aaspaas.customer.domain.model.Product
import com.aaspaas.customer.domain.model.SearchQuery
import com.aaspaas.customer.domain.model.SearchResult
import com.aaspaas.customer.domain.model.Store

interface ProductRepository {
    suspend fun search(query: SearchQuery): Result<SearchResult>
    suspend fun getProductById(id: String): Result<Product>
    suspend fun getNearbyProducts(latitude: Double, longitude: Double, radiusKm: Double): Result<List<Product>>
    suspend fun getProductsByStore(storeId: String): Result<List<Product>>
}

interface StoreRepository {
    suspend fun getNearbyStores(latitude: Double, longitude: Double, radiusKm: Double): Result<List<Store>>
    suspend fun getStoreById(id: String): Result<Store>
}
