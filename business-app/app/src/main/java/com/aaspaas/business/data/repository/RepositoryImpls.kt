package com.aaspaas.business.data.repository

import com.aaspaas.business.BuildConfig
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

    override suspend fun sendOtp(mobile: String): Result<Unit> {
        if (BuildConfig.USE_MOCK_AUTH) return Result.success(Unit)
        return safeApiCall { api.sendOtp(SendOtpRequest(mobile)) }.toResult().map { }
    }

    override suspend fun verifyOtp(mobile: String, otp: String): Result<Retailer> {
        if (BuildConfig.USE_MOCK_AUTH) {
            val mock = Retailer("mock_r_001", "Test Owner", "Test Business", mobile, null, VerificationStatus.PENDING)
            _currentRetailer.value = mock
            tokenProvider.saveToken("mock_business_token")
            return Result.success(mock)
        }
        return safeApiCall { api.verifyOtp(VerifyOtpRequest(mobile, otp)) }
            .toResult()
            .mapCatching { response ->
                val data = response.data ?: error("Empty auth response")
                tokenProvider.saveToken(data.token)
                val retailer = data.retailer.toDomain()
                _currentRetailer.value = retailer
                retailer
            }
    }

    override suspend fun registerBusiness(request: RegisterBusinessRequest): Result<Retailer> =
        safeApiCall {
            api.register(
                com.aaspaas.business.data.remote.RegisterBusinessRequest(
                    ownerName = request.ownerName, businessName = request.businessName,
                    mobile = request.mobile, email = request.email, category = request.category,
                    address = request.address, latitude = request.latitude,
                    longitude = request.longitude, openingHours = request.openingHours
                )
            )
        }.toResult().mapCatching { response ->
            val data = response.data ?: error("Empty register response")
            tokenProvider.saveToken(data.token)
            val retailer = data.retailer.toDomain()
            _currentRetailer.value = retailer
            retailer
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

    override suspend fun confirmReservation(reservationId: String): Result<Unit> =
        safeApiCall { api.confirmReservation(reservationId) }.toResult().map { }

    override suspend fun completeReservation(reservationId: String): Result<Unit> =
        safeApiCall { api.completeReservation(reservationId) }.toResult().map { }

    override suspend fun cancelReservation(reservationId: String): Result<Unit> =
        safeApiCall { api.cancelReservation(reservationId) }.toResult().map { }

    override suspend fun getDashboardStats(): Result<DashboardStats> =
        safeApiCall { api.getDashboardStats() }.toResult().mapCatching { it.data?.toDomain() ?: error("No stats") }
}
