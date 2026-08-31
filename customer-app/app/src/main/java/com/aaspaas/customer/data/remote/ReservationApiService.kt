package com.aaspaas.customer.data.remote

import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ReservationApiService {
    @POST("reservations")
    suspend fun createReservation(@Body request: CreateReservationRequest): ApiResponse<ReservationDto>

    @GET("reservations")
    suspend fun getMyReservations(): ApiResponse<ReservationsListResponse>

    @GET("reservations/{id}")
    suspend fun getReservationById(@Path("id") id: String): ApiResponse<ReservationWrapper>

    @POST("reservations/{id}/cancel")
    suspend fun cancelReservation(@Path("id") id: String): ApiResponse<Unit>
}
