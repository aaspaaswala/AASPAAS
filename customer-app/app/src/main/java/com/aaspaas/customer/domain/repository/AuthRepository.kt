package com.aaspaas.customer.domain.repository

import com.aaspaas.customer.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUser: Flow<User?>
    suspend fun login(mobile: String, dob: String? = null): Result<User>
    suspend fun register(name: String, mobile: String, email: String? = null, dob: String? = null): Result<User>
    suspend fun requestPhoneOtp(phone: String, name: String? = null, dob: String? = null): Result<Unit>
    suspend fun verifyPhoneOtp(phone: String, otp: String, name: String? = null, email: String? = null, dob: String? = null): Result<User>
    suspend fun requestEmailOtp(email: String, name: String? = null, dob: String? = null): Result<Unit>
    suspend fun verifyEmailOtp(email: String, otp: String, name: String? = null, dob: String? = null): Result<User>
    suspend fun socialLogin(provider: String, idToken: String, name: String? = null): Result<User>
    suspend fun logout()
    suspend fun isLoggedIn(): Boolean
}
