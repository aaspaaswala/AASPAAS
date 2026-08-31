package com.aaspaas.customer.data.repository

import com.aaspaas.customer.BuildConfig
import com.aaspaas.customer.SessionManager
import com.aaspaas.customer.core.network.TokenProvider
import com.aaspaas.customer.core.network.safeApiCall
import com.aaspaas.customer.core.network.toResult
import com.aaspaas.customer.data.model.toDomain
import com.aaspaas.customer.data.remote.AuthApiService
import com.aaspaas.customer.data.remote.SendOtpRequest
import com.aaspaas.customer.data.remote.VerifyOtpRequest
import com.aaspaas.customer.domain.model.User
import com.aaspaas.customer.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val api: AuthApiService,
    private val tokenProvider: TokenProvider,
    private val sessionManager: SessionManager
) : AuthRepository {

    private val _currentUser = MutableStateFlow<User?>(null)
    override val currentUser: Flow<User?> = _currentUser.asStateFlow()

    override suspend fun sendOtp(mobile: String): Result<Unit> {
        // In mock/debug mode, skip real API call
        if (BuildConfig.USE_MOCK_AUTH) return Result.success(Unit)
        return safeApiCall { api.sendOtp(SendOtpRequest(mobile)) }.toResult().map { }
    }

    override suspend fun verifyOtp(mobile: String, otp: String): Result<User> {
        if (BuildConfig.USE_MOCK_AUTH) {
            val mockUser = User(
                id = "mock_user_001",
                name = "Test User",
                mobile = mobile,
                email = null
            )
            _currentUser.value = mockUser
            tokenProvider.saveToken("mock_jwt_token")
            sessionManager.setLoggedIn(true)
            return Result.success(mockUser)
        }
        return safeApiCall { api.verifyOtp(VerifyOtpRequest(mobile, otp)) }
            .toResult()
            .mapCatching { response ->
                val authData = response.data ?: error("Empty auth response")
                tokenProvider.saveToken(authData.token)
                val user = authData.user.toDomain()
                _currentUser.value = user
                sessionManager.setLoggedIn(true)
                user
            }
    }

    override suspend fun logout() {
        tokenProvider.clearToken()
        sessionManager.clearSession()
        _currentUser.value = null
    }

    override suspend fun isLoggedIn(): Boolean = tokenProvider.getToken() != null
}
