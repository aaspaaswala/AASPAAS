package com.aaspaas.customer.core.database.dao

import androidx.room.*
import com.aaspaas.customer.core.database.entity.ReservationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReservationDao {

    @Query("SELECT * FROM reservations ORDER BY createdAt DESC")
    fun getAllReservations(): Flow<List<ReservationEntity>>

    @Query("SELECT * FROM reservations WHERE status IN ('PENDING', 'CONFIRMED', 'ACTIVE') ORDER BY expiresAt ASC")
    fun getActiveReservations(): Flow<List<ReservationEntity>>

    @Query("SELECT * FROM reservations WHERE id = :id")
    fun getReservationById(id: String): Flow<ReservationEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReservation(reservation: ReservationEntity)

    @Update
    suspend fun updateReservation(reservation: ReservationEntity)

    @Query("DELETE FROM reservations WHERE id = :id")
    suspend fun deleteReservation(id: String)

    @Query("DELETE FROM reservations")
    suspend fun deleteAllReservations()
}
