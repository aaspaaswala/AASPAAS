package com.aaspaas.business.feature.store.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aaspaas.business.domain.model.Store
import com.aaspaas.business.domain.repository.StoreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class StoreUiState(
    val isLoading: Boolean = false,
    val store: Store? = null,
    val isSaved: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class StoreViewModel @Inject constructor(
    private val repository: StoreRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(StoreUiState(isLoading = true))
    val uiState = _uiState.asStateFlow()

    init { loadStore() }

    fun loadStore() {
        viewModelScope.launch {
            _uiState.value = StoreUiState(isLoading = true)
            repository.getMyStore()
                .onSuccess { _uiState.value = StoreUiState(store = it) }
                .onFailure { _uiState.value = StoreUiState(error = it.message) }
        }
    }

    fun updateStore(store: Store) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, error = null)
            repository.updateStore(store)
                .onSuccess { _uiState.value = StoreUiState(store = it, isSaved = true) }
                .onFailure { _uiState.value = _uiState.value.copy(isLoading = false, error = it.message) }
        }
    }
}
