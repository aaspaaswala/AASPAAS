package com.aaspaas.customer.feature.wishlist.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aaspaas.customer.domain.model.Product
import com.aaspaas.customer.domain.model.Store
import com.aaspaas.customer.domain.repository.WishlistRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class WishlistUiState(
    val isLoading: Boolean = false,
    val products: List<Product> = emptyList(),
    val stores: List<Store> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class WishlistViewModel @Inject constructor(
    private val wishlistRepository: WishlistRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WishlistUiState(isLoading = true))
    val uiState = _uiState.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _uiState.value = WishlistUiState(isLoading = true)
            val productsResult = wishlistRepository.getWishlistedProducts()
            val storesResult = wishlistRepository.getWishlistedStores()
            _uiState.value = WishlistUiState(
                products = productsResult.getOrElse { emptyList() },
                stores = storesResult.getOrElse { emptyList() },
                error = productsResult.exceptionOrNull()?.message
                    ?: storesResult.exceptionOrNull()?.message
            )
        }
    }

    fun toggleProductWishlist(productId: String, isWishlisted: Boolean) {
        viewModelScope.launch {
            if (isWishlisted) {
                wishlistRepository.addToWishlist(productId)
            } else {
                wishlistRepository.removeFromWishlist(productId)
            }
            refresh()
        }
    }

    fun toggleStoreWishlist(storeId: String, isWishlisted: Boolean) {
        viewModelScope.launch {
            if (isWishlisted) {
                wishlistRepository.addStoreToWishlist(storeId)
            } else {
                wishlistRepository.removeStoreFromWishlist(storeId)
            }
            refresh()
        }
    }

    fun isProductWishlisted(productId: String) =
        wishlistRepository.isProductWishlisted(productId)

    fun isStoreWishlisted(storeId: String) =
        wishlistRepository.isStoreWishlisted(storeId)
}
