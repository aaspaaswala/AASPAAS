package com.aaspaas.customer.domain.repository

import com.aaspaas.customer.domain.model.Reservation
import com.aaspaas.customer.domain.model.ReservationStatus
import kotlinx.coroutines.flow.Flow

interface ReservationRepository {
    suspend fun createReservation(variantId: String): Result<Reservation>
    suspend fun cancelReservation(reservationId: String): Result<Unit>
    suspend fun getReservationById(id: String): Result<Reservation>
    suspend fun fetchMyReservations(): Result<List<Reservation>>
    fun getMyReservations(): Flow<List<Reservation>>
    fun getActiveReservations(): Flow<List<Reservation>>
}
