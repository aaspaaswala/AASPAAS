package com.aaspaas.business.feature.dashboard.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aaspaas.business.domain.model.DashboardStats
import com.aaspaas.business.domain.repository.BusinessReservationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardUiState(
    val isLoading: Boolean = false,
    val stats: DashboardStats? = null,
    val error: String? = null
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val reservationRepository: BusinessReservationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState(isLoading = true))
    val uiState = _uiState.asStateFlow()

    init { loadStats() }

    fun loadStats() {
        viewModelScope.launch {
            _uiState.value = DashboardUiState(isLoading = true)
            reservationRepository.getDashboardStats()
                .onSuccess { _uiState.value = DashboardUiState(stats = it) }
                .onFailure { _uiState.value = DashboardUiState(error = it.message) }
        }
    }
}
