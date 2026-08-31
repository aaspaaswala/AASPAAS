package com.aaspaas.customer.data.remote

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ProductApiService {
    @GET("products/search")
    suspend fun search(
        @Query("q") query: String,
        @Query("lat") latitude: Double?,
        @Query("lng") longitude: Double?,
        @Query("radius") radiusKm: Double,
        @Query("category") category: String?,
        @Query("minPrice") minPrice: Double?,
        @Query("maxPrice") maxPrice: Double?
    ): ApiResponse<SearchResultDto>

    @GET("products/{id}")
    suspend fun getProductById(@Path("id") id: String): ApiResponse<ProductDto>

    @GET("products/nearby")
    suspend fun getNearbyProducts(
        @Query("lat") latitude: Double,
        @Query("lng") longitude: Double,
        @Query("radius") radiusKm: Double
    ): ApiResponse<List<ProductDto>>

    @GET("stores/{storeId}/products")
    suspend fun getProductsByStore(@Path("storeId") storeId: String): ApiResponse<List<ProductDto>>
}

interface StoreApiService {
    @GET("stores/nearby")
    suspend fun getNearbyStores(
        @Query("lat") latitude: Double,
        @Query("lng") longitude: Double,
        @Query("radius") radiusKm: Double
    ): ApiResponse<List<StoreDto>>

    @GET("stores/{id}")
    suspend fun getStoreById(@Path("id") id: String): ApiResponse<StoreDto>
}
