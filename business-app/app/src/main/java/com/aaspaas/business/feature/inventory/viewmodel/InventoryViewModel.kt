package com.aaspaas.business.feature.inventory.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aaspaas.business.domain.model.Product
import com.aaspaas.business.domain.repository.ProductManagementRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class InventoryUiState(
    val isLoading: Boolean = false,
    val products: List<Product> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class InventoryViewModel @Inject constructor(
    private val repository: ProductManagementRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(InventoryUiState(isLoading = true))
    val uiState = _uiState.asStateFlow()

    init { load() }

    fun load() {
        viewModelScope.launch {
            _uiState.value = InventoryUiState(isLoading = true)
            repository.getMyProducts()
                .onSuccess { _uiState.value = InventoryUiState(products = it) }
                .onFailure { _uiState.value = InventoryUiState(error = it.message) }
        }
    }

    fun updateStock(inventoryId: String, newStock: Int) {
        viewModelScope.launch {
            repository.updateInventory(inventoryId, newStock).onSuccess { load() }
        }
    }
}
