package com.aaspaas.customer.feature.store.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aaspaas.customer.domain.model.Product
import com.aaspaas.customer.domain.model.Store
import com.aaspaas.customer.domain.repository.ProductRepository
import com.aaspaas.customer.domain.repository.StoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class StoreDetailUiState(
    val isLoading: Boolean = false,
    val store: Store? = null,
    val products: List<Product> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class StoreDetailViewModel @Inject constructor(
    private val storeRepository: StoreRepository,
    private val productRepository: ProductRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StoreDetailUiState(isLoading = true))
    val uiState = _uiState.asStateFlow()

    fun load(storeId: String) {
        viewModelScope.launch {
            _uiState.value = StoreDetailUiState(isLoading = true)
            val storeResult = storeRepository.getStoreById(storeId)
            storeResult.onFailure { _uiState.value = StoreDetailUiState(error = it.message); return@launch }
            val store = storeResult.getOrThrow()
            val products = productRepository.getProductsByStore(storeId).getOrElse { emptyList() }
            _uiState.value = StoreDetailUiState(store = store, products = products)
        }
    }
}
