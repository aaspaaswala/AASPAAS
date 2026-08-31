package com.aaspaas.customer.data.repository

import com.aaspaas.customer.BuildConfig
import com.aaspaas.customer.SessionManager
import com.aaspaas.customer.core.network.TokenProvider
import com.aaspaas.customer.core.network.safeApiCall
import com.aaspaas.customer.core.network.toResult
import com.aaspaas.customer.data.model.toDomain
import com.aaspaas.customer.data.remote.AuthApiService
import com.aaspaas.customer.data.remote.CustomerAuthRequest
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

    override suspend fun login(mobile: String, name: String?): Result<User> {
        if (BuildConfig.USE_MOCK_AUTH) {
            val mockUser = User(id = "mock_user_001", name = name ?: "Test User", mobile = mobile, email = null)
            _currentUser.value = mockUser
            tokenProvider.saveToken("mock_jwt_token")
            sessionManager.setLoggedIn(true)
            return Result.success(mockUser)
        }
        return safeApiCall { api.login(CustomerAuthRequest(phone = mobile, name = name)) }
            .toResult()
            .mapCatching { response ->
                val authData = response.data ?: error("Empty auth response")
                tokenProvider.saveTokens(authData.accessToken, authData.refreshToken)
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
