package com.aaspaas.customer.feature.store.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aaspaas.customer.domain.model.Store
import com.aaspaas.customer.domain.repository.StoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class StoreMapUiState(
    val isLoading: Boolean = false,
    val store: Store? = null,
    val error: String? = null
)

@HiltViewModel
class StoreMapViewModel @Inject constructor(
    private val storeRepository: StoreRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StoreMapUiState(isLoading = true))
    val uiState = _uiState.asStateFlow()

    fun load(storeId: String) {
        viewModelScope.launch {
            _uiState.value = StoreMapUiState(isLoading = true)
            storeRepository.getStoreById(storeId)
                .onSuccess { store ->
                    _uiState.value = StoreMapUiState(store = store)
                }
                .onFailure { _uiState.value = StoreMapUiState(error = it.message) }
        }
    }
}
