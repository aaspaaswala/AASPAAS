package com.aaspaas.business.feature.reservation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aaspaas.business.domain.model.Reservation
import com.aaspaas.business.domain.repository.BusinessReservationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BusinessReservationDetailUiState(
    val isLoading: Boolean = false,
    val reservation: Reservation? = null,
    val error: String? = null
)

@HiltViewModel
class BusinessReservationDetailViewModel @Inject constructor(
    private val repository: BusinessReservationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(BusinessReservationDetailUiState(isLoading = true))
    val uiState = _uiState.asStateFlow()

    fun load(reservationId: String) {
        viewModelScope.launch {
            _uiState.value = BusinessReservationDetailUiState(isLoading = true)
            repository.getReservationById(reservationId)
                .onSuccess { _uiState.value = BusinessReservationDetailUiState(reservation = it) }
                .onFailure { _uiState.value = BusinessReservationDetailUiState(error = it.message) }
        }
    }

    fun confirm(id: String) { viewModelScope.launch { repository.confirmReservation(id).onSuccess { load(id) } } }
    fun markReady(id: String) { viewModelScope.launch { repository.markReservationReady(id).onSuccess { load(id) } } }
    fun reject(id: String, reason: String? = null) { viewModelScope.launch { repository.rejectReservation(id, reason).onSuccess { load(id) } } }
    fun complete(id: String) { viewModelScope.launch { repository.completeReservation(id).onSuccess { load(id) } } }
    fun cancel(id: String) { viewModelScope.launch { repository.cancelReservation(id).onSuccess { load(id) } } }
    fun markNoShow(id: String) { viewModelScope.launch { repository.markNoShow(id).onSuccess { load(id) } } }
}
