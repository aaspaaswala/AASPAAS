package com.aaspaas.customer.feature.reservation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aaspaas.customer.domain.model.Product
import com.aaspaas.customer.domain.model.Reservation
import com.aaspaas.customer.domain.model.ReservationStatus
import com.aaspaas.customer.domain.repository.ProductRepository
import com.aaspaas.customer.domain.repository.ReservationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.inject.Inject

// ── Confirm screen ────────────────────────────────────────────────────────────

data class ReservationConfirmUiState(
    val isLoading: Boolean = false,
    val product: Product? = null,
    val variantId: String = "",
    val confirmedReservationId: String? = null,
    val error: String? = null
)

@HiltViewModel
class ReservationConfirmViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val reservationRepository: ReservationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReservationConfirmUiState(isLoading = true))
    val uiState = _uiState.asStateFlow()

    fun load(variantId: String) {
        // We need the product that contains this variant — search by variant id
        // For now we store variantId and load product details via a search or direct call
        _uiState.value = ReservationConfirmUiState(isLoading = false, variantId = variantId)
    }

    fun confirmReservation(variantId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            reservationRepository.createReservation(variantId)
                .onSuccess { reservation ->
                    _uiState.value = _uiState.value.copy(isLoading = false, confirmedReservationId = reservation.id)
                }
                .onFailure {
                    _uiState.value = _uiState.value.copy(isLoading = false, error = it.message ?: "Reservation failed")
                }
        }
    }
}

// ── Detail / countdown screen ─────────────────────────────────────────────────

data class ReservationDetailUiState(
    val isLoading: Boolean = false,
    val reservation: Reservation? = null,
    val remainingSeconds: Long = 0,
    val error: String? = null
)

@HiltViewModel
class ReservationDetailViewModel @Inject constructor(
    private val reservationRepository: ReservationRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReservationDetailUiState(isLoading = true))
    val uiState = _uiState.asStateFlow()

    fun load(reservationId: String) {
        viewModelScope.launch {
            reservationRepository.getReservationById(reservationId)
                .onSuccess { reservation ->
                    _uiState.value = ReservationDetailUiState(reservation = reservation)
                    startCountdown(reservation)
                }
                .onFailure { _uiState.value = ReservationDetailUiState(error = it.message) }
        }
    }

    private fun startCountdown(reservation: Reservation) {
        if (reservation.status !in listOf(ReservationStatus.ACTIVE, ReservationStatus.CONFIRMED, ReservationStatus.PENDING)) return
        viewModelScope.launch {
            while (true) {
                val remaining = ChronoUnit.SECONDS.between(Instant.now(), reservation.expiresAt)
                if (remaining <= 0) {
                    _uiState.value = _uiState.value.copy(remainingSeconds = 0)
                    break
                }
                _uiState.value = _uiState.value.copy(remainingSeconds = remaining)
                delay(1000)
            }
        }
    }

    fun cancelReservation(reservationId: String) {
        viewModelScope.launch {
            reservationRepository.cancelReservation(reservationId)
                .onSuccess { load(reservationId) }
                .onFailure { _uiState.value = _uiState.value.copy(error = it.message) }
        }
    }
}

// ── My Reservations list ──────────────────────────────────────────────────────

data class MyReservationsUiState(
    val isLoading: Boolean = false,
    val active: List<Reservation> = emptyList(),
    val completed: List<Reservation> = emptyList(),
    val cancelled: List<Reservation> = emptyList(),
    val expired: List<Reservation> = emptyList()
)

@HiltViewModel
class MyReservationsViewModel @Inject constructor(
    private val reservationRepository: ReservationRepository
) : ViewModel() {

    val uiState: StateFlow<MyReservationsUiState> =
        reservationRepository.getMyReservations()
            .map { list ->
                MyReservationsUiState(
                    active = list.filter { it.status in listOf(ReservationStatus.ACTIVE, ReservationStatus.CONFIRMED, ReservationStatus.PENDING) },
                    completed = list.filter { it.status == ReservationStatus.COMPLETED },
                    cancelled = list.filter { it.status == ReservationStatus.CANCELLED },
                    expired = list.filter { it.status == ReservationStatus.EXPIRED }
                )
            }
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), MyReservationsUiState(isLoading = true))
}
