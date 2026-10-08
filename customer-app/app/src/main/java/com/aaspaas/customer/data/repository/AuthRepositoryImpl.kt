package com.aaspaas.customer.data.repository

import com.aaspaas.customer.BuildConfig
import com.aaspaas.customer.SessionManager
import com.aaspaas.customer.core.network.TokenProvider
import com.aaspaas.customer.core.network.safeApiCall
import com.aaspaas.customer.core.network.toResult
import com.aaspaas.customer.data.model.toDomain
import com.aaspaas.customer.data.remote.*
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

    override suspend fun login(mobile: String, dob: String?): Result<User> {
        val normalizedDob = dob?.trim()?.ifBlank { null }

        if (BuildConfig.USE_MOCK_AUTH) {
            val mockUser = User(
                id = "mock_user_001",
                name = "Test User",
                mobile = mobile,
                email = null,
                dob = normalizedDob
            )
            _currentUser.value = mockUser
            tokenProvider.saveToken("mock_jwt_token")
            sessionManager.saveProfile(mockUser.name, mockUser.email, mockUser.dob)
            sessionManager.setLoggedIn(true)
            return Result.success(mockUser)
        }
        return safeApiCall { api.login(CustomerAuthRequest(phone = mobile, name = null, email = null, dob = normalizedDob)) }
            .toResult()
            .mapCatching { response ->
                val authData = response.data ?: error("Empty auth response")
                tokenProvider.saveTokens(authData.accessToken, authData.refreshToken)
                val user = authData.user.toDomain()
                _currentUser.value = user
                sessionManager.saveProfile(user.name, user.email, user.dob)
                sessionManager.setLoggedIn(true)
                user
            }
    }

    override suspend fun requestEmailOtp(email: String, name: String?, dob: String?): Result<Unit> {
        if (BuildConfig.USE_MOCK_AUTH) return Result.success(Unit)
        return safeApiCall { api.requestEmailOtp(EmailOtpRequest(email.trim(), name, dob)) }
            .toResult().map { }
    }

    override suspend fun requestPhoneOtp(phone: String, name: String?, dob: String?): Result<Unit> {
        val normalizedPhone = phone.trim()
        if (BuildConfig.USE_MOCK_AUTH) return Result.success(Unit)
        return safeApiCall { api.requestPhoneOtp(PhoneOtpRequest(normalizedPhone, name, dob)) }
            .toResult().map { }
    }

    override suspend fun verifyPhoneOtp(phone: String, otp: String, name: String?, email: String?, dob: String?): Result<User> {
        val normalizedPhone = phone.trim()
        val normalizedEmail = email?.trim()?.ifBlank { null }
        val normalizedDob = dob?.trim()?.ifBlank { null }
        if (BuildConfig.USE_MOCK_AUTH) {
            val mockUser = User(
                id = "mock_phone_user",
                name = name ?: "Test User",
                mobile = normalizedPhone,
                email = normalizedEmail,
                dob = normalizedDob
            )
            _currentUser.value = mockUser
            tokenProvider.saveToken("mock_phone_token")
            sessionManager.saveProfile(mockUser.name, mockUser.email, mockUser.dob)
            sessionManager.setLoggedIn(true)
            return Result.success(mockUser)
        }
        return safeApiCall {
            api.verifyPhoneOtp(PhoneOtpVerifyRequest(normalizedPhone, otp, name, normalizedEmail, normalizedDob))
        }.toResult().mapCatching { response -> persistAuth(response) }
    }

    override suspend fun verifyEmailOtp(email: String, otp: String, name: String?, dob: String?): Result<User> {
        if (BuildConfig.USE_MOCK_AUTH) {
            val mockUser = User("mock_email_user", name ?: "Test User", "", email, dob)
            _currentUser.value = mockUser
            tokenProvider.saveToken("mock_email_token")
            sessionManager.saveProfile(mockUser.name, mockUser.email, mockUser.dob)
            sessionManager.setLoggedIn(true)
            return Result.success(mockUser)
        }
        return safeApiCall { api.verifyEmailOtp(EmailOtpVerifyRequest(email.trim(), otp, name, dob)) }
            .toResult().mapCatching { response -> persistAuth(response) }
    }

    override suspend fun socialLogin(provider: String, idToken: String, name: String?): Result<User> {
        return safeApiCall { api.socialLogin(SocialAuthRequest(provider, idToken, name)) }
            .toResult().mapCatching { response -> persistAuth(response) }
    }

    private suspend fun persistAuth(response: com.aaspaas.customer.data.remote.ApiResponse<AuthResponse>): User {
        val authData = response.data ?: error("Empty auth response")
        tokenProvider.saveTokens(authData.accessToken, authData.refreshToken)
        val user = authData.user.toDomain()
        _currentUser.value = user
        sessionManager.saveProfile(user.name, user.email, user.dob)
        sessionManager.setLoggedIn(true)
        return user
    }

    override suspend fun register(name: String, mobile: String, email: String?, dob: String?): Result<User> {
        val normalizedName = name.trim()
        val normalizedEmail = email?.trim()?.ifBlank { null }
        val normalizedDob = dob?.trim()?.ifBlank { null }

        if (BuildConfig.USE_MOCK_AUTH) {
            val mockUser = User(
                id = "mock_user_002",
                name = normalizedName,
                mobile = mobile,
                email = normalizedEmail,
                dob = normalizedDob
            )
            _currentUser.value = mockUser
            tokenProvider.saveToken("mock_jwt_token")
            sessionManager.saveProfile(mockUser.name, mockUser.email, mockUser.dob)
            sessionManager.setLoggedIn(true)
            return Result.success(mockUser)
        }
        return safeApiCall { api.login(CustomerAuthRequest(phone = mobile, name = normalizedName, email = normalizedEmail, dob = normalizedDob)) }
            .toResult()
            .mapCatching { response ->
                val authData = response.data ?: error("Empty auth response")
                tokenProvider.saveTokens(authData.accessToken, authData.refreshToken)
                val user = authData.user.toDomain()
                _currentUser.value = user
                sessionManager.saveProfile(user.name, user.email, user.dob)
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
