package com.aaspaas.customer.data.repository

import com.aaspaas.customer.core.network.safeApiCall
import com.aaspaas.customer.core.network.toResult
import com.aaspaas.customer.data.model.toDomain
import com.aaspaas.customer.data.remote.CreateReservationRequest
import com.aaspaas.customer.data.remote.ReservationApiService
import com.aaspaas.customer.domain.model.Reservation
import com.aaspaas.customer.domain.repository.ReservationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReservationRepositoryImpl @Inject constructor(
    private val api: ReservationApiService
) : ReservationRepository {

    private val _reservations = MutableStateFlow<List<Reservation>>(emptyList())

    override suspend fun createReservation(variantId: String): Result<Reservation> =
        safeApiCall { api.createReservation(CreateReservationRequest(variantId = variantId, quantity = 1)) }
            .toResult()
            .mapCatching { response ->
                val reservation = response.data?.toDomain() ?: error("Empty reservation response")
                _reservations.value = _reservations.value + reservation
                reservation
            }

    override suspend fun cancelReservation(reservationId: String): Result<Unit> =
        safeApiCall { api.cancelReservation(reservationId) }
            .toResult()
            .map { }

    override suspend fun getReservationById(id: String): Result<Reservation> =
        safeApiCall { api.getReservationById(id) }
            .toResult()
            .mapCatching { response ->
                response.data?.toDomain() ?: error("Reservation not found")
            }

    override fun getMyReservations(): Flow<List<Reservation>> = _reservations

    override suspend fun fetchMyReservations(): Result<List<Reservation>> =
        safeApiCall { api.getMyReservations() }
            .toResult()
            .mapCatching { response ->
                // Backend returns list directly as data
                val list = response.data?.map { it.toDomain() } ?: emptyList()
                _reservations.value = list
                list
            }

    override fun getActiveReservations(): Flow<List<Reservation>> =
        _reservations.map { list ->
            list.filter { it.status.name in listOf("PENDING", "CONFIRMED", "ACTIVE") }
        }
}
