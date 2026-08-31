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
    val isRegistered: Boolean = false
)

@HiltViewModel
class BusinessAuthViewModel @Inject constructor(
    private val authRepository: BusinessAuthRepository,
    private val sessionManager: BusinessSessionManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState = _uiState.asStateFlow()

    fun login(mobile: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)
            authRepository.login(mobile)
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

    suspend fun isLoggedIn(): Boolean = authRepository.isLoggedIn()
}
