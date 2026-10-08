package com.aaspaas.customer.data.remote

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ProductApiService {
    // GET /api/v1/products/search?q=&category=&brand=&limit=&skip=&latitude=&longitude=&radiusKm=&minPrice=&maxPrice=
    @GET("products/search")
    suspend fun search(
        @Query("q") query: String,
        @Query("category") category: String? = null,
        @Query("brand") brand: String? = null,
        @Query("limit") limit: Int? = null,
        @Query("skip") skip: Int? = null,
        @Query("latitude") latitude: Double? = null,
        @Query("longitude") longitude: Double? = null,
        @Query("radiusKm") radiusKm: Double? = null,
        @Query("minPrice") minPrice: Double? = null,
        @Query("maxPrice") maxPrice: Double? = null
    ): ApiResponse<SearchResultDto>

    // GET /api/v1/products/:id
    @GET("products/{id}")
    suspend fun getProductById(@Path("id") id: String): ApiResponse<ProductDto>

    // GET /api/v1/products/:id/price-comparison?latitude=&longitude=&radius=
    @GET("products/{id}/price-comparison")
    suspend fun getPriceComparison(
        @Path("id") id: String,
        @Query("latitude") latitude: Double? = null,
        @Query("longitude") longitude: Double? = null,
        @Query("radius") radiusKm: Double = 10.0
    ): ApiResponse<List<PriceComparisonDto>>

    // GET /api/v1/products/nearby?latitude=&longitude=&radius=&q=&category=&brand=&minPrice=&maxPrice=
    @GET("products/nearby")
    suspend fun getNearbyProducts(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("radius") radiusKm: Double,
        @Query("q") q: String? = null,
        @Query("category") category: String? = null,
        @Query("brand") brand: String? = null,
        @Query("minPrice") minPrice: Double? = null,
        @Query("maxPrice") maxPrice: Double? = null
    ): ApiResponse<List<ProductDto>>

    // GET /api/v1/stores/:storeId/products
    @GET("stores/{storeId}/products")
    suspend fun getProductsByStore(@Path("storeId") storeId: String): ApiResponse<List<ProductDto>>
}

interface StoreApiService {
    // GET /api/v1/stores/nearby?latitude=&longitude=&radius=&category=
    @GET("stores/nearby")
    suspend fun getNearbyStores(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("radius") radiusKm: Double,
        @Query("category") category: String? = null
    ): ApiResponse<List<StoreDto>>

    // GET /api/v1/stores/:id
    @GET("stores/{id}")
    suspend fun getStoreById(@Path("id") id: String): ApiResponse<StoreDto>
}