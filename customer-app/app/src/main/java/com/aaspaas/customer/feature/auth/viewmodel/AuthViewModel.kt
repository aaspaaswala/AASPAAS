package com.aaspaas.customer.feature.auth.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aaspaas.customer.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isVerified: Boolean = false,
    val otpSent: Boolean = false
)

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState = _uiState.asStateFlow()

    // Exposed so OTP screen can display which phone/email OTP was sent to
    var currentEmail: String = ""
        private set

    var currentPhone: String = ""
        private set

    fun login(mobile: String, dob: String? = null) {
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)
            authRepository.login(mobile, dob)
                .onSuccess { _uiState.value = AuthUiState(isVerified = true) }
                .onFailure { _uiState.value = AuthUiState(error = it.message) }
        }
    }

    fun register(name: String, mobile: String, email: String? = null, dob: String? = null) {
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)
            authRepository.register(name, mobile, email, dob)
                .onSuccess { _uiState.value = AuthUiState(isVerified = true) }
                .onFailure { _uiState.value = AuthUiState(error = it.message) }
        }
    }

    fun requestPhoneOtp(phone: String, name: String? = null, dob: String? = null) {
        currentPhone = formatPhone(phone)
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)
            authRepository.requestPhoneOtp(currentPhone, name, dob)
                .onSuccess { _uiState.value = AuthUiState(otpSent = true) }
                .onFailure { _uiState.value = AuthUiState(error = it.message) }
        }
    }

    fun verifyPhoneOtp(phone: String, otp: String, name: String? = null, email: String? = null, dob: String? = null) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            authRepository.verifyPhoneOtp(phone, otp, name, email, dob)
                .onSuccess { _uiState.value = AuthUiState(isVerified = true) }
                .onFailure { _uiState.value = AuthUiState(otpSent = true, error = it.message) }
        }
    }

    fun requestEmailOtp(email: String, name: String?, dob: String?) {
        currentEmail = email
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)
            authRepository.requestEmailOtp(email, name, dob)
                .onSuccess { _uiState.value = AuthUiState(otpSent = true) }
                .onFailure { _uiState.value = AuthUiState(error = it.message) }
        }
    }

    fun verifyEmailOtp(email: String, otp: String, name: String?, dob: String?) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            authRepository.verifyEmailOtp(email, otp, name, dob)
                .onSuccess { _uiState.value = AuthUiState(isVerified = true) }
                .onFailure { _uiState.value = AuthUiState(otpSent = true, error = it.message) }
        }
    }

    fun socialLogin(provider: String, idToken: String, name: String? = null) {
        viewModelScope.launch {
            _uiState.value = AuthUiState(isLoading = true)
            authRepository.socialLogin(provider, idToken, name)
                .onSuccess { _uiState.value = AuthUiState(isVerified = true) }
                .onFailure { _uiState.value = AuthUiState(error = it.message) }
        }
    }

    fun clearError() { _uiState.value = _uiState.value.copy(error = null) }

    fun resetOtp() { _uiState.value = AuthUiState() }

    companion object {
        fun formatPhone(raw: String): String {
            val digits = raw.filter(Char::isDigit)
            if (digits.length == 10) return "+91$digits"
            if (digits.startsWith("91") && digits.length == 12) return "+$digits"
            return if (digits.startsWith("+")) raw.trim() else "+91$digits"
        }
    }
}
