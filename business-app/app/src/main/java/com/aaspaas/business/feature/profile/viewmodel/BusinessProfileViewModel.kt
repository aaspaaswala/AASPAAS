package com.aaspaas.business.feature.profile.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aaspaas.business.BusinessSessionManager
import com.aaspaas.business.domain.repository.BusinessAuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BusinessProfileViewModel @Inject constructor(
    private val authRepository: BusinessAuthRepository,
    private val sessionManager: BusinessSessionManager
) : ViewModel() {

    val retailer = authRepository.currentRetailer
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun logout(onDone: () -> Unit) {
        viewModelScope.launch {
            authRepository.logout()
            sessionManager.clearSession()
            onDone()
        }
    }
}
