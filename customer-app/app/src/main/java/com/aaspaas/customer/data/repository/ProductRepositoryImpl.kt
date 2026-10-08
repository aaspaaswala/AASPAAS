package com.aaspaas.customer.data.repository

import com.aaspaas.customer.core.database.dao.ProductDao
import com.aaspaas.customer.core.database.dao.StoreDao
import com.aaspaas.customer.core.database.mapper.toDomain
import com.aaspaas.customer.core.database.entity.ProductEntity
import com.aaspaas.customer.core.network.safeApiCall
import com.aaspaas.customer.core.network.toResult
import com.aaspaas.customer.data.model.toDomain
import com.aaspaas.customer.data.model.toEntity
import com.aaspaas.customer.data.remote.ProductApiService
import com.aaspaas.customer.data.remote.StoreApiService
import com.aaspaas.customer.domain.model.PriceComparisonEntry
import com.aaspaas.customer.domain.model.Product
import com.aaspaas.customer.domain.model.SearchQuery
import com.aaspaas.customer.domain.model.SearchResult
import com.aaspaas.customer.domain.model.Store
import com.aaspaas.customer.domain.repository.ProductRepository
import com.aaspaas.customer.domain.repository.StoreRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepositoryImpl @Inject constructor(
    private val api: ProductApiService,
    private val storeApi: StoreApiService,
    private val productDao: ProductDao,
    private val storeDao: StoreDao
) : ProductRepository {

    override suspend fun search(query: SearchQuery): Result<SearchResult> {
        val cachedProducts = productDao.getProductById(query.text)
            .firstOrNull()
            ?.let { listOf(it) }
            ?.map { it.toDomain() }
            .orEmpty()

        val cachedStores = storeDao.getStoreById(query.text)
            .firstOrNull()
            ?.let { listOf(it) }
            ?.map { it.toDomain() }
            .orEmpty()

        return if (cachedProducts.isNotEmpty() || cachedStores.isNotEmpty()) {
            Result.success(SearchResult(
                products = cachedProducts,
                stores = cachedStores,
                totalCount = cachedProducts.size + cachedStores.size
            ))
        } else {
            fetchFromApi(query)
        }
    }

    private suspend fun fetchFromApi(query: SearchQuery): Result<SearchResult> =
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
            val products = data.products.map { it.toDomain() }
            val stores = data.stores.map { it.toDomain() }

            val productEntities = products.map { it.toEntity() }
            productDao.insertProducts(productEntities)

            val storeEntities = stores.map { it.toEntity() }
            storeDao.insertStores(storeEntities)

            SearchResult(
                products = products,
                stores = stores,
                totalCount = data.totalCount
            )
        }

    override suspend fun getProductById(id: String): Result<Product> {
        val cached = productDao.getProductById(id).firstOrNull()
        if (cached != null) {
            return Result.success(cached.toDomain())
        }
        return fetchProductFromApi(id)
    }

    private suspend fun fetchProductFromApi(id: String): Result<Product> =
        safeApiCall { api.getProductById(id) }
            .toResult()
            .mapCatching { response ->
                val product = response.data?.toDomain() ?: error("Product not found")
                productDao.insertProducts(listOf(product.toEntity()))
                product
            }

    override suspend fun getNearbyProducts(
        latitude: Double, longitude: Double, radiusKm: Double
    ): Result<List<Product>> {
        val cachedProducts = productDao.getProductsByStore("")
            .first()
            .filter { it.storeDistanceKm != null }

        if (cachedProducts.isNotEmpty()) {
            return Result.success(cachedProducts.map { it.toDomain() })
        }

        return fetchNearbyFromApi(latitude, longitude, radiusKm)
    }

    private suspend fun fetchNearbyFromApi(
        latitude: Double, longitude: Double, radiusKm: Double
    ): Result<List<Product>> =
        safeApiCall { api.getNearbyProducts(latitude, longitude, radiusKm) }
            .toResult()
            .mapCatching { response ->
                val products = response.data?.map { it.toDomain() } ?: emptyList()
                productDao.insertProducts(products.map { it.toEntity() })
                products
            }

    override suspend fun getProductsByStore(storeId: String): Result<List<Product>> {
        val cached = productDao.getProductsByStore(storeId).first()
        if (cached.isNotEmpty()) {
            return Result.success(cached.map { it.toDomain() })
        }

        return fetchProductsByStoreFromApi(storeId)
    }

    private suspend fun fetchProductsByStoreFromApi(storeId: String): Result<List<Product>> =
        safeApiCall { api.getProductsByStore(storeId) }
            .toResult()
            .mapCatching { response ->
                val products = response.data?.map { it.toDomain() } ?: emptyList()
                productDao.insertProducts(products.map { it.toEntity() })
                products
            }

    override suspend fun getPriceComparison(
        productId: String,
        latitude: Double?,
        longitude: Double?,
        radiusKm: Double
    ): Result<List<PriceComparisonEntry>> =
        safeApiCall {
            api.getPriceComparison(
                id = productId,
                latitude = latitude,
                longitude = longitude,
                radiusKm = radiusKm
            )
        }.toResult().mapCatching { response ->
            response.data?.map { it.toDomain() } ?: emptyList()
        }
}

@Singleton
class StoreRepositoryImpl @Inject constructor(
    private val api: StoreApiService,
    private val storeDao: StoreDao
) : StoreRepository {

    override suspend fun getNearbyStores(
        latitude: Double, longitude: Double, radiusKm: Double
    ): Result<List<Store>> {
        val minLat = latitude - radiusKm / 111.0
        val maxLat = latitude + radiusKm / 111.0
        val minLng = longitude - radiusKm / (111.0 * Math.cos(Math.toRadians(latitude)))
        val maxLng = longitude + radiusKm / (111.0 * Math.cos(Math.toRadians(latitude)))

        val cachedStores = storeDao.getNearbyStores(minLat, maxLat, minLng, maxLng).first()
        if (cachedStores.isNotEmpty()) {
            return Result.success(cachedStores.map { it.toDomain() })
        }

        return fetchNearbyFromApi(latitude, longitude, radiusKm)
    }

    private suspend fun fetchNearbyFromApi(
        latitude: Double, longitude: Double, radiusKm: Double
    ): Result<List<Store>> =
        safeApiCall { api.getNearbyStores(latitude, longitude, radiusKm) }
            .toResult()
            .mapCatching { response ->
                val stores = response.data?.map { it.toDomain() } ?: emptyList()
                storeDao.insertStores(stores.map { it.toEntity() })
                stores
            }

    override suspend fun getStoreById(id: String): Result<Store> {
        val cached = storeDao.getStoreById(id).firstOrNull()
        if (cached != null) {
            return Result.success(cached.toDomain())
        }

        return fetchStoreFromApi(id)
    }

    private suspend fun fetchStoreFromApi(id: String): Result<Store> =
        safeApiCall { api.getStoreById(id) }
            .toResult()
            .mapCatching { response ->
                val store = response.data?.toDomain() ?: error("Store not found")
                val storeEntity = store.toEntity()
                storeDao.insertStore(storeEntity)
                store
            }
}
