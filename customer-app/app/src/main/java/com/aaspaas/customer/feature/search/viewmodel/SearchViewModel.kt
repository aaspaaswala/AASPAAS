package com.aaspaas.customer.feature.search.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aaspaas.customer.core.location.LocationProvider
import com.aaspaas.customer.domain.model.Product
import com.aaspaas.customer.domain.model.SearchQuery
import com.aaspaas.customer.domain.model.Store
import com.aaspaas.customer.domain.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SearchUiState(
    val isLoading: Boolean = false,
    val products: List<Product> = emptyList(),
    val stores: List<Store> = emptyList(),
    val error: String? = null,
    val hasSearched: Boolean = false
)

@OptIn(FlowPreview::class)
@HiltViewModel
class SearchViewModel @Inject constructor(
    private val productRepository: ProductRepository,
    private val locationProvider: LocationProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState = _uiState.asStateFlow()

    private val _query = MutableStateFlow("")
    val query = _query.asStateFlow()

    init {
        _query
            .debounce(400)
            .filter { it.length >= 2 }
            .onEach { search(it) }
            .launchIn(viewModelScope)
    }

    fun onQueryChange(q: String) { _query.value = q }

    fun search(q: String = _query.value) {
        if (q.isBlank()) return
        viewModelScope.launch {
            _uiState.value = SearchUiState(isLoading = true, hasSearched = true)
            val location = locationProvider.getCurrentLocation().getOrNull()
            val result = productRepository.search(
                SearchQuery(text = q, latitude = location?.latitude, longitude = location?.longitude)
            )
            result
                .onSuccess { _uiState.value = SearchUiState(products = it.products, stores = it.stores, hasSearched = true) }
                .onFailure { _uiState.value = SearchUiState(error = it.message, hasSearched = true) }
        }
    }
}
