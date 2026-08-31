package com.aaspaas.customer.data.repository

import com.aaspaas.customer.core.network.safeApiCall
import com.aaspaas.customer.core.network.toResult
import com.aaspaas.customer.data.model.toDomain
import com.aaspaas.customer.data.remote.ProductApiService
import com.aaspaas.customer.data.remote.StoreApiService
import com.aaspaas.customer.domain.model.Product
import com.aaspaas.customer.domain.model.SearchQuery
import com.aaspaas.customer.domain.model.SearchResult
import com.aaspaas.customer.domain.model.Store
import com.aaspaas.customer.domain.repository.ProductRepository
import com.aaspaas.customer.domain.repository.StoreRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepositoryImpl @Inject constructor(
    private val api: ProductApiService
) : ProductRepository {

    override suspend fun search(query: SearchQuery): Result<SearchResult> =
        safeApiCall {
            api.search(
                query = query.text,
                latitude = query.latitude,
                longitude = query.longitude,
                radiusKm = query.radiusKm,
                category = query.category,
                minPrice = query.minPrice,
                maxPrice = query.maxPrice
            )
        }.toResult().mapCatching { response ->
            val data = response.data ?: error("Empty search response")
            SearchResult(
                products = data.products.map { it.toDomain() },
                stores = data.stores.map { it.toDomain() },
                totalCount = data.totalCount
            )
        }

    override suspend fun getProductById(id: String): Result<Product> =
        safeApiCall { api.getProductById(id) }
            .toResult()
            .mapCatching { it.data?.toDomain() ?: error("Product not found") }

    override suspend fun getNearbyProducts(
        latitude: Double, longitude: Double, radiusKm: Double
    ): Result<List<Product>> =
        safeApiCall { api.getNearbyProducts(latitude, longitude, radiusKm) }
            .toResult()
            .mapCatching { it.data?.map { dto -> dto.toDomain() } ?: emptyList() }

    override suspend fun getProductsByStore(storeId: String): Result<List<Product>> =
        safeApiCall { api.getProductsByStore(storeId) }
            .toResult()
            .mapCatching { it.data?.map { dto -> dto.toDomain() } ?: emptyList() }
}

@Singleton
class StoreRepositoryImpl @Inject constructor(
    private val api: StoreApiService
) : StoreRepository {

    override suspend fun getNearbyStores(
        latitude: Double, longitude: Double, radiusKm: Double
    ): Result<List<Store>> =
        safeApiCall { api.getNearbyStores(latitude, longitude, radiusKm) }
            .toResult()
            .mapCatching { it.data?.map { dto -> dto.toDomain() } ?: emptyList() }

    override suspend fun getStoreById(id: String): Result<Store> =
        safeApiCall { api.getStoreById(id) }
            .toResult()
            .mapCatching { it.data?.toDomain() ?: error("Store not found") }
}
