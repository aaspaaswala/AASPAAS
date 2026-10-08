package com.aaspaas.customer.feature.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aaspaas.customer.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val authRepository: AuthRepository
) : ViewModel() {

    enum class Destination { HOME, AUTH, ONBOARDING }

    private val _destination = MutableStateFlow<Destination?>(null)
    val destination = _destination.asStateFlow()

    init {
        viewModelScope.launch {
            _destination.value = when {
                authRepository.isLoggedIn() -> Destination.HOME
                else -> Destination.ONBOARDING
            }
        }
    }
}
