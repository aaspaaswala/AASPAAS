package com.aaspaas.customer.feature.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aaspaas.customer.core.location.LocationProvider
import com.aaspaas.customer.core.location.UserLocation
import com.aaspaas.customer.domain.model.Product
import com.aaspaas.customer.domain.model.Store
import com.aaspaas.customer.domain.repository.ProductRepository
import com.aaspaas.customer.domain.repository.StoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class HomeUiState(
    val isLoading: Boolean = false,
    val location: UserLocation? = null,
    val nearbyProducts: List<Product> = emptyList(),
    val nearbyStores: List<Store> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val locationProvider: LocationProvider,
    private val productRepository: ProductRepository,
    private val storeRepository: StoreRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState(isLoading = true))
    val uiState = _uiState.asStateFlow()

    init { loadHomeData() }

    fun loadHomeData() {
        viewModelScope.launch {
            _uiState.value = HomeUiState(isLoading = true)
            val locationResult = locationProvider.getCurrentLocation()
            val location = locationResult.getOrNull()

            if (location != null) {
                val products = productRepository.getNearbyProducts(location.latitude, location.longitude, 10.0).getOrElse { emptyList() }
                val stores = storeRepository.getNearbyStores(location.latitude, location.longitude, 10.0).getOrElse { emptyList() }
                _uiState.value = HomeUiState(location = location, nearbyProducts = products, nearbyStores = stores)
            } else {
                _uiState.value = HomeUiState(error = "Could not get location. Please enable location access.")
            }
        }
    }
}
