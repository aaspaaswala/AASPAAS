package com.aaspaas.business.feature.reservation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aaspaas.business.domain.model.Reservation
import com.aaspaas.business.domain.model.ReservationStatus
import com.aaspaas.business.domain.repository.BusinessReservationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BusinessReservationsUiState(
    val active: List<Reservation> = emptyList(),
    val completed: List<Reservation> = emptyList(),
    val cancelled: List<Reservation> = emptyList(),
    val expired: List<Reservation> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class BusinessReservationsViewModel @Inject constructor(
    private val repository: BusinessReservationRepository
) : ViewModel() {

    val uiState: StateFlow<BusinessReservationsUiState> =
        repository.getReservations()
            .map { list ->
                BusinessReservationsUiState(
                    isLoading = false,
                    active = list.filter { it.status in listOf(ReservationStatus.READY, ReservationStatus.CONFIRMED, ReservationStatus.PENDING) },
                    completed = list.filter { it.status == ReservationStatus.COMPLETED },
                    cancelled = list.filter { it.status in listOf(ReservationStatus.CANCELLED, ReservationStatus.REJECTED, ReservationStatus.NO_SHOW) },
                    expired = list.filter { it.status == ReservationStatus.EXPIRED }
                )
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), BusinessReservationsUiState())

    fun confirm(id: String) { viewModelScope.launch { repository.confirmReservation(id) } }
    fun markReady(id: String) { viewModelScope.launch { repository.markReservationReady(id) } }
    fun reject(id: String, reason: String? = null) { viewModelScope.launch { repository.rejectReservation(id, reason) } }
    fun complete(id: String) { viewModelScope.launch { repository.completeReservation(id) } }
    fun cancel(id: String) { viewModelScope.launch { repository.cancelReservation(id) } }
    fun markNoShow(id: String) { viewModelScope.launch { repository.markNoShow(id) } }
}
