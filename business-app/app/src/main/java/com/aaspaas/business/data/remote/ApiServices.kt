package com.aaspaas.business.data.remote

import retrofit2.http.*

interface BusinessAuthApiService {
    @POST("business/auth/login")
    suspend fun login(@Body request: LoginRequest): ApiResponse<Any>

    @POST("business/auth/phone/request")
    suspend fun requestPhoneOtp(@Body request: PhoneOtpRequest): ApiResponse<Any>

    @POST("business/auth/phone/verify")
    suspend fun verifyPhoneOtp(@Body request: PhoneOtpVerifyRequest): ApiResponse<AuthResponse>

    @POST("business/auth/register")
    suspend fun register(@Body request: RegisterBusinessRequest): ApiResponse<AuthResponse>

    @POST("business/auth/email/request")
    suspend fun requestEmailOtp(@Body request: EmailOtpRequest): ApiResponse<Any>

    @POST("business/auth/email/verify")
    suspend fun verifyEmailOtp(@Body request: EmailOtpVerifyRequest): ApiResponse<AuthResponse>

    @POST("business/auth/social")
    suspend fun socialLogin(@Body request: SocialAuthRequest): ApiResponse<AuthResponse>
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

    @GET("business/reservations/{id}")
    suspend fun getReservationById(@Path("id") id: String): ApiResponse<ReservationDto>

    @POST("business/reservations/{id}/confirm")
    suspend fun confirmReservation(@Path("id") id: String): ApiResponse<Unit>

    @POST("business/reservations/{id}/ready")
    suspend fun markReservationReady(@Path("id") id: String): ApiResponse<Unit>

    @POST("business/reservations/{id}/reject")
    suspend fun rejectReservation(@Path("id") id: String, @Body request: RejectReservationRequest): ApiResponse<Unit>

    @POST("business/reservations/{id}/complete")
    suspend fun completeReservation(@Path("id") id: String): ApiResponse<Unit>

    @POST("business/reservations/{id}/cancel")
    suspend fun cancelReservation(@Path("id") id: String): ApiResponse<Unit>

    @POST("business/reservations/{id}/no-show")
    suspend fun markNoShow(@Path("id") id: String): ApiResponse<Unit>

    @GET("business/dashboard")
    suspend fun getDashboardStats(): ApiResponse<DashboardStatsDto>
}
