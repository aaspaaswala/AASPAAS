package com.aaspaas.business.data.repository

import com.aaspaas.business.core.network.DataStoreTokenProvider
import com.aaspaas.business.core.network.TokenProvider
import com.aaspaas.business.core.network.safeApiCall
import com.aaspaas.business.core.network.toResult
import com.aaspaas.business.data.model.toDomain
import com.aaspaas.business.data.remote.*
import com.aaspaas.business.domain.model.*
import com.aaspaas.business.domain.model.AddProductRequest
import com.aaspaas.business.domain.repository.*
import com.aaspaas.business.domain.repository.RegisterBusinessRequest
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BusinessAuthRepositoryImpl @Inject constructor(
    private val api: BusinessAuthApiService,
    private val tokenProvider: TokenProvider
) : BusinessAuthRepository {

    private val _currentRetailer = MutableStateFlow<Retailer?>(null)
    override val currentRetailer: Flow<Retailer?> = _currentRetailer.asStateFlow()

    override suspend fun login(phone: String): Result<Unit> {
        return safeApiCall { api.login(LoginRequest(phone.trim())) }
            .toResult()
            .map {
                // Business login initiates phone OTP verification; it does not establish a logged-in session yet.
            }
    }

    override suspend fun requestPhoneOtp(phone: String): Result<Unit> {
        return safeApiCall { api.requestPhoneOtp(PhoneOtpRequest(phone.trim())) }
            .toResult().map { }
    }

    override suspend fun verifyPhoneOtp(phone: String, otp: String): Result<Retailer> {
        return safeApiCall { api.verifyPhoneOtp(PhoneOtpVerifyRequest(phone.trim(), otp)) }
            .toResult().mapCatching { response -> persistAuth(response) }
    }

    override suspend fun requestEmailOtp(email: String): Result<Unit> {
        return safeApiCall { api.requestEmailOtp(EmailOtpRequest(email.trim())) }
            .toResult().map { }
    }

    override suspend fun verifyEmailOtp(email: String, otp: String): Result<Retailer> {
        return safeApiCall { api.verifyEmailOtp(EmailOtpVerifyRequest(email.trim(), otp)) }
            .toResult().mapCatching { response -> persistAuth(response) }
    }

    override suspend fun socialLogin(provider: String, idToken: String): Result<Retailer> {
        return safeApiCall { api.socialLogin(SocialAuthRequest(provider, idToken)) }
            .toResult().mapCatching { response -> persistAuth(response) }
    }

    private suspend fun persistAuth(response: ApiResponse<AuthResponse>): Retailer {
        val data = response.data ?: error("Empty auth response")
        tokenProvider.saveTokens(data.accessToken, data.refreshToken)
        val retailer = data.retailer.toDomain()
        _currentRetailer.value = retailer
        return retailer
    }

    override suspend fun registerBusiness(request: RegisterBusinessRequest): Result<Retailer> {
        return safeApiCall {
            api.register(
                com.aaspaas.business.data.remote.RegisterBusinessRequest(
                    ownerName = request.ownerName, businessName = request.businessName,
                    phone = request.mobile, email = request.email, category = request.category,
                    address = request.address, latitude = request.latitude,
                    longitude = request.longitude, openingHours = request.openingHours
                )
            )
        }.toResult().mapCatching { response ->
            val data = response.data ?: error("Empty register response")
            tokenProvider.saveTokens(data.accessToken, data.refreshToken)
            val retailer = data.retailer.toDomain()
            _currentRetailer.value = retailer
            retailer
        }
    }

    override suspend fun logout() {
        tokenProvider.clearToken()
        _currentRetailer.value = null
    }

    override suspend fun isLoggedIn(): Boolean = tokenProvider.getToken() != null
}

@Singleton
class StoreRepositoryImpl @Inject constructor(
    private val api: BusinessStoreApiService
) : StoreRepository {
    override suspend fun getMyStore(): Result<Store> =
        safeApiCall { api.getMyStore() }.toResult().mapCatching { it.data?.toDomain() ?: error("No store") }

    override suspend fun updateStore(store: Store): Result<Store> =
        safeApiCall {
            api.updateStore(StoreDto(store.id, store.name, store.address, store.latitude, store.longitude,
                store.phone, store.openingHours, store.categories, store.imageUrl,
                store.verificationStatus.name))
        }.toResult().mapCatching { it.data?.toDomain() ?: error("Update failed") }
}

@Singleton
class ProductManagementRepositoryImpl @Inject constructor(
    private val api: BusinessProductApiService
) : ProductManagementRepository {
    override suspend fun getMyProducts(): Result<List<Product>> =
        safeApiCall { api.getMyProducts() }.toResult().mapCatching { it.data?.map { d -> d.toDomain() } ?: emptyList() }

    override suspend fun addProduct(request: AddProductRequest): Result<Product> =
        safeApiCall {
            api.addProduct(com.aaspaas.business.data.remote.AddProductRequest(
                name = request.name, brand = request.brand, description = request.description,
                category = request.category,
                variants = request.variants.map { v -> com.aaspaas.business.data.remote.AddVariantRequest(v.size, v.color, v.price, v.stock) }
            ))
        }.toResult().mapCatching { it.data?.toDomain() ?: error("Add product failed") }

    override suspend fun updateProduct(productId: String, request: AddProductRequest): Result<Product> =
        safeApiCall {
            api.updateProduct(productId, com.aaspaas.business.data.remote.AddProductRequest(
                name = request.name, brand = request.brand, description = request.description,
                category = request.category,
                variants = request.variants.map { v -> com.aaspaas.business.data.remote.AddVariantRequest(v.size, v.color, v.price, v.stock) }
            ))
        }.toResult().mapCatching { it.data?.toDomain() ?: error("Update product failed") }

    override suspend fun deleteProduct(productId: String): Result<Unit> =
        safeApiCall { api.deleteProduct(productId) }.toResult().map { }

    override suspend fun updateInventory(inventoryId: String, stock: Int): Result<Inventory> =
        safeApiCall { api.updateInventory(inventoryId, UpdateInventoryRequest(stock)) }
            .toResult().mapCatching { it.data?.toDomain() ?: error("Update inventory failed") }
}

@Singleton
class BusinessReservationRepositoryImpl @Inject constructor(
    private val api: BusinessReservationApiService
) : BusinessReservationRepository {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val _reservations = MutableStateFlow<List<Reservation>>(emptyList())

    override fun getReservations(): Flow<List<Reservation>> {
        scope.launch {
            safeApiCall { api.getReservations() }.toResult()
                .onSuccess { response -> _reservations.value = response.data?.map { it.toDomain() } ?: emptyList() }
        }
        return _reservations
    }

    override suspend fun getReservationById(reservationId: String): Result<Reservation> =
        safeApiCall { api.getReservationById(reservationId) }
            .toResult()
            .mapCatching { it.data?.toDomain() ?: error("Reservation not found") }

    override suspend fun confirmReservation(reservationId: String): Result<Unit> =
        safeApiCall { api.confirmReservation(reservationId) }.toResult().map { }

    override suspend fun markReservationReady(reservationId: String): Result<Unit> =
        safeApiCall { api.markReservationReady(reservationId) }.toResult().map { }

    override suspend fun rejectReservation(reservationId: String, reason: String?): Result<Unit> =
        safeApiCall { api.rejectReservation(reservationId, RejectReservationRequest(reason)) }.toResult().map { }

    override suspend fun completeReservation(reservationId: String): Result<Unit> =
        safeApiCall { api.completeReservation(reservationId) }.toResult().map { }

    override suspend fun cancelReservation(reservationId: String): Result<Unit> =
        safeApiCall { api.cancelReservation(reservationId) }.toResult().map { }

    override suspend fun markNoShow(reservationId: String): Result<Unit> =
        safeApiCall { api.markNoShow(reservationId) }.toResult().map { }

    override suspend fun getDashboardStats(): Result<DashboardStats> =
        safeApiCall { api.getDashboardStats() }.toResult().mapCatching { it.data?.toDomain() ?: error("No stats") }
}
