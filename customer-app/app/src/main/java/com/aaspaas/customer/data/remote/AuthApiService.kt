package com.aaspaas.customer.data.remote

import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApiService {
    @POST("auth/customer")
    suspend fun login(@Body request: CustomerAuthRequest): ApiResponse<AuthResponse>

    @POST("auth/customer/phone/request")
    suspend fun requestPhoneOtp(@Body request: PhoneOtpRequest): ApiResponse<Any>

    @POST("auth/customer/phone/verify")
    suspend fun verifyPhoneOtp(@Body request: PhoneOtpVerifyRequest): ApiResponse<AuthResponse>

    @POST("auth/customer/email/request")
    suspend fun requestEmailOtp(@Body request: EmailOtpRequest): ApiResponse<Any>

    @POST("auth/customer/email/verify")
    suspend fun verifyEmailOtp(@Body request: EmailOtpVerifyRequest): ApiResponse<AuthResponse>

    @POST("auth/customer/social")
    suspend fun socialLogin(@Body request: SocialAuthRequest): ApiResponse<AuthResponse>
}
