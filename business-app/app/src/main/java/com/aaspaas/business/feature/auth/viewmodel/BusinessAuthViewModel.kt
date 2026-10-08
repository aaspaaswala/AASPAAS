package com.aaspaas.business.feature.auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aaspaas.business.BusinessSessionManager
import com.aaspaas.business.domain.repository.BusinessAuthRepository
import com.aaspaas.business.domain.repository.RegisterBusinessRequest
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isVerified: Boolean = false,
    val isRegistered: Boolean = false,
    val otpSent: Boolean = false
)

@HiltViewModel
class BusinessAuthViewModel @Inject constructor(
    private val authRepository: BusinessAuthRepository,
    private val sessionManager: BusinessSessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState = _uiState.asStateFlow()

    fun login(phone: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)
            authRepository.login(phone)
                .onSuccess { _uiState.value = AuthUiState(otpSent = true, error = null) }
                .onFailure { _uiState.value = AuthUiState(error = it.message) }
        }
    }

    fun requestPhoneOtp(phone: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)
            authRepository.requestPhoneOtp(phone)
                .onSuccess { _uiState.value = AuthUiState(otpSent = true, error = null) }
                .onFailure { _uiState.value = AuthUiState(error = it.message) }
        }
    }

    fun verifyPhoneOtp(phone: String, otp: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            authRepository.verifyPhoneOtp(phone, otp)
                .onSuccess {
                    sessionManager.setLoggedIn(true)
                    _uiState.value = AuthUiState(isVerified = true)
                }
                .onFailure { _uiState.value = AuthUiState(otpSent = true, error = it.message) }
        }
    }

    fun requestEmailOtp(email: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)
            authRepository.requestEmailOtp(email)
                .onSuccess { _uiState.value = AuthUiState(otpSent = true) }
                .onFailure { _uiState.value = AuthUiState(error = it.message) }
        }
    }

    fun verifyEmailOtp(email: String, otp: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            authRepository.verifyEmailOtp(email, otp)
                .onSuccess {
                    sessionManager.setLoggedIn(true)
                    _uiState.value = AuthUiState(isVerified = true)
                }
                .onFailure { _uiState.value = AuthUiState(otpSent = true, error = it.message) }
        }
    }

    fun socialLogin(provider: String, idToken: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)
            authRepository.socialLogin(provider, idToken)
                .onSuccess {
                    sessionManager.setLoggedIn(true)
                    _uiState.value = AuthUiState(isVerified = true)
                }
                .onFailure { _uiState.value = AuthUiState(error = it.message) }
        }
    }

    fun register(
        ownerName: String, businessName: String, mobile: String, email: String?,
        category: String, address: String, latitude: Double, longitude: Double, openingHours: String
    ) {
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)
            authRepository.registerBusiness(
                RegisterBusinessRequest(ownerName, businessName, mobile, email, category, address, latitude, longitude, openingHours)
            )
                .onSuccess {
                    sessionManager.setLoggedIn(true)
                    _uiState.value = AuthUiState(isRegistered = true)
                }
                .onFailure { _uiState.value = AuthUiState(error = it.message) }
        }
    }

    fun clearError() { _uiState.value = _uiState.value.copy(error = null) }

    fun resetAuthFlow() { _uiState.value = AuthUiState() }

    suspend fun isLoggedIn(): Boolean = authRepository.isLoggedIn()
}
