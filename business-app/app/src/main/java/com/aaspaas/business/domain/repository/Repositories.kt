package com.aaspaas.business.domain.repository

import com.aaspaas.business.domain.model.*
import kotlinx.coroutines.flow.Flow

interface BusinessAuthRepository {
    val currentRetailer: Flow<Retailer?>
    suspend fun login(phone: String): Result<Unit>
    suspend fun requestPhoneOtp(phone: String): Result<Unit>
    suspend fun verifyPhoneOtp(phone: String, otp: String): Result<Retailer>
    suspend fun requestEmailOtp(email: String): Result<Unit>
    suspend fun verifyEmailOtp(email: String, otp: String): Result<Retailer>
    suspend fun socialLogin(provider: String, idToken: String): Result<Retailer>
    suspend fun registerBusiness(request: RegisterBusinessRequest): Result<Retailer>
    suspend fun logout()
    suspend fun isLoggedIn(): Boolean
}

data class RegisterBusinessRequest(
    val ownerName: String,
    val businessName: String,
    val mobile: String,
    val email: String?,
    val category: String,
    val address: String,
    val latitude: Double,
    val longitude: Double,
    val openingHours: String
)

interface StoreRepository {
    suspend fun getMyStore(): Result<Store>
    suspend fun updateStore(store: Store): Result<Store>
}

interface ProductManagementRepository {
    suspend fun getMyProducts(): Result<List<Product>>
    suspend fun addProduct(request: AddProductRequest): Result<Product>
    suspend fun updateProduct(productId: String, request: AddProductRequest): Result<Product>
    suspend fun deleteProduct(productId: String): Result<Unit>
    suspend fun updateInventory(inventoryId: String, stock: Int): Result<Inventory>
}

interface BusinessReservationRepository {
    fun getReservations(): Flow<List<Reservation>>
    suspend fun getReservationById(reservationId: String): Result<Reservation>
    suspend fun confirmReservation(reservationId: String): Result<Unit>
    suspend fun markReservationReady(reservationId: String): Result<Unit>
    suspend fun rejectReservation(reservationId: String, reason: String?): Result<Unit>
    suspend fun completeReservation(reservationId: String): Result<Unit>
    suspend fun cancelReservation(reservationId: String): Result<Unit>
    suspend fun markNoShow(reservationId: String): Result<Unit>
    suspend fun getDashboardStats(): Result<DashboardStats>
}
