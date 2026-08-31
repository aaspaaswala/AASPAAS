package com.aaspaas.customer.data.remote

import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("auth/customer")
    suspend fun login(@Body request: CustomerAuthRequest): ApiResponse<AuthResponse>
}
