package com.aaspaas.business.data.remote

import retrofit2.http.*

interface BusinessAuthApiService {
    @POST("business/auth/send-otp")
    suspend fun sendOtp(@Body request: SendOtpRequest): ApiResponse<Unit>

    @POST("business/auth/verify-otp")
    suspend fun verifyOtp(@Body request: VerifyOtpRequest): ApiResponse<AuthResponse>

    @POST("business/auth/register")
    suspend fun register(@Body request: RegisterBusinessRequest): ApiResponse<AuthResponse>
}

interface BusinessStoreApiService {
    @GET("business/store")
    suspend fun getMyStore(): ApiResponse<StoreDto>

    @PUT("business/store")
    suspend fun updateStore(@Body store: StoreDto): ApiResponse<StoreDto>
}

interface BusinessProductApiService {
    @GET("business/products")
    suspend fun getMyProducts(): ApiResponse<List<ProductDto>>

    @POST("business/products")
    suspend fun addProduct(@Body request: AddProductRequest): ApiResponse<ProductDto>

    @PUT("business/products/{id}")
    suspend fun updateProduct(@Path("id") id: String, @Body request: AddProductRequest): ApiResponse<ProductDto>

    @DELETE("business/products/{id}")
    suspend fun deleteProduct(@Path("id") id: String): ApiResponse<Unit>

    @PUT("business/inventory/{id}")
    suspend fun updateInventory(@Path("id") id: String, @Body request: UpdateInventoryRequest): ApiResponse<InventoryDto>
}

interface BusinessReservationApiService {
    @GET("business/reservations")
    suspend fun getReservations(): ApiResponse<List<ReservationDto>>

    @POST("business/reservations/{id}/confirm")
    suspend fun confirmReservation(@Path("id") id: String): ApiResponse<Unit>

    @POST("business/reservations/{id}/complete")
    suspend fun completeReservation(@Path("id") id: String): ApiResponse<Unit>

    @POST("business/reservations/{id}/cancel")
    suspend fun cancelReservation(@Path("id") id: String): ApiResponse<Unit>

    @GET("business/dashboard")
    suspend fun getDashboardStats(): ApiResponse<DashboardStatsDto>
}
