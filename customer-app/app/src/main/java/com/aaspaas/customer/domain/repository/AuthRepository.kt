package com.aaspaas.customer.domain.repository

import com.aaspaas.customer.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUser: Flow<User?>
    suspend fun login(mobile: String, name: String? = null): Result<User>
    suspend fun logout()
    suspend fun isLoggedIn(): Boolean
}
