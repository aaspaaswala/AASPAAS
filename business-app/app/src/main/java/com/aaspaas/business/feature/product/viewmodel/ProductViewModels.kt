package com.aaspaas.business.feature.product.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aaspaas.business.domain.model.AddProductRequest
import com.aaspaas.business.domain.model.AddVariantRequest
import com.aaspaas.business.domain.model.Product
import com.aaspaas.business.domain.repository.ProductManagementRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProductListUiState(
    val isLoading: Boolean = false,
    val products: List<Product> = emptyList(),
    val error: String? = null
)

data class AddEditUiState(
    val isLoading: Boolean = false,
    val product: Product? = null,
    val isSaved: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val repository: ProductManagementRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductListUiState(isLoading = true))
    val uiState = _uiState.asStateFlow()

    init { loadProducts() }

    fun loadProducts() {
        viewModelScope.launch {
            _uiState.value = ProductListUiState(isLoading = true)
            repository.getMyProducts()
                .onSuccess { _uiState.value = ProductListUiState(products = it) }
                .onFailure { _uiState.value = ProductListUiState(error = it.message) }
        }
    }

    fun deleteProduct(productId: String) {
        viewModelScope.launch {
            repository.deleteProduct(productId).onSuccess { loadProducts() }
        }
    }
}

@HiltViewModel
class AddEditProductViewModel @Inject constructor(
    private val repository: ProductManagementRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddEditUiState())
    val uiState = _uiState.asStateFlow()

    fun loadProduct(productId: String) {
        viewModelScope.launch {
            _uiState.value = AddEditUiState(isLoading = true)
            repository.getMyProducts()
                .onSuccess { products ->
                    val product = products.find { it.id == productId }
                    _uiState.value = AddEditUiState(product = product)
                }
                .onFailure { _uiState.value = AddEditUiState(error = it.message) }
        }
    }

    fun saveProduct(
        productId: String?,
        name: String, brand: String?, description: String?, category: String,
        size: String?, color: String?, price: Double, stock: Int
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            val request = AddProductRequest(
                name = name, brand = brand?.ifBlank { null }, description = description?.ifBlank { null },
                category = category,
                variants = listOf(AddVariantRequest(size = size?.ifBlank { null }, color = color?.ifBlank { null }, price = price, stock = stock))
            )
            val result = if (productId != null) repository.updateProduct(productId, request)
                         else repository.addProduct(request)
            result
                .onSuccess { _uiState.value = AddEditUiState(isSaved = true) }
                .onFailure { _uiState.value = _uiState.value.copy(isLoading = false, error = it.message) }
        }
    }
}
