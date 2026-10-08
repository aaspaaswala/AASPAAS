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
    val brands: List<String> = emptyList(),
    val categories: List<String> = emptyList(),
    val error: String? = null,
    val hasSearched: Boolean = false,
    val recentSearches: List<String> = emptyList()
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

    // In-memory recent searches (max 5); replace with DataStore for persistence
    private val recentSearches = mutableListOf<String>()

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
            _uiState.value = _uiState.value.copy(isLoading = true, hasSearched = true)
            val location = locationProvider.getCurrentLocation().getOrNull()
            val result = productRepository.search(
                SearchQuery(text = q, latitude = location?.latitude, longitude = location?.longitude)
            )
            // Save to recent searches
            if (!recentSearches.contains(q)) {
                recentSearches.add(0, q)
                if (recentSearches.size > 5) recentSearches.removeLastOrNull()
            }
            result
                .onSuccess {
                    val brands = it.products.mapNotNull { p -> p.brand }.distinct()
                    val categories = it.products.map { p -> p.category }.distinct()
                    _uiState.value = SearchUiState(
                        products = it.products,
                        stores = it.stores,
                        brands = brands,
                        categories = categories,
                        hasSearched = true,
                        recentSearches = recentSearches.toList()
                    )
                }
                .onFailure {
                    _uiState.value = SearchUiState(
                        error = it.message,
                        hasSearched = true,
                        recentSearches = recentSearches.toList()
                    )
                }
        }
    }

    fun clearRecentSearches() {
        recentSearches.clear()
        _uiState.value = _uiState.value.copy(recentSearches = emptyList())
    }
}
