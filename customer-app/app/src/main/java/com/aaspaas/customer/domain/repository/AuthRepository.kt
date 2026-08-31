package com.aaspaas.customer.domain.repository

import com.aaspaas.customer.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUser: Flow<User?>
    suspend fun sendOtp(mobile: String): Result<Unit>
    suspend fun verifyOtp(mobile: String, otp: String): Result<User>
    suspend fun logout()
    suspend fun isLoggedIn(): Boolean
}
