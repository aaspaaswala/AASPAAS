package com.aaspaas.customer.feature.product.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aaspaas.customer.core.location.LocationProvider
import com.aaspaas.customer.domain.model.PriceComparisonEntry
import com.aaspaas.customer.domain.model.Product
import com.aaspaas.customer.domain.model.ProductVariant
import com.aaspaas.customer.domain.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProductDetailUiState(
    val isLoading: Boolean = false,
    val product: Product? = null,
    val selectedVariant: ProductVariant? = null,
    val priceComparison: List<PriceComparisonEntry> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val locationProvider: LocationProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductDetailUiState(isLoading = true))
    val uiState = _uiState.asStateFlow()

    fun load(productId: String, variantId: String) {
        viewModelScope.launch {
            _uiState.value = ProductDetailUiState(isLoading = true)
            productRepository.getProductById(productId)
                .onSuccess { product ->
                    val variant = product.variants.find { it.id == variantId } ?: product.variants.firstOrNull()
                    _uiState.value = ProductDetailUiState(product = product, selectedVariant = variant)
                    loadPriceComparison(productId, product.id)
                }
                .onFailure { _uiState.value = ProductDetailUiState(error = it.message) }
        }
    }

    private fun loadPriceComparison(productId: String, variantProductId: String) {
        viewModelScope.launch {
            val location = locationProvider.getCurrentLocation().getOrNull()
            productRepository.getPriceComparison(
                productId = productId,
                latitude = location?.latitude,
                longitude = location?.longitude
            )
                .onSuccess { comparison ->
                    _uiState.value = _uiState.value.copy(priceComparison = comparison)
                }
                .onFailure { /* silently fail — price comparison is optional */ }
        }
    }

    fun selectVariant(variant: ProductVariant) {
        _uiState.value = _uiState.value.copy(selectedVariant = variant)
    }
}
