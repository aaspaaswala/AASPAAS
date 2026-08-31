package com.aaspaas.customer.feature.search.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aaspaas.customer.core.ui.components.EmptyScreen
import com.aaspaas.customer.core.ui.components.ErrorScreen
import com.aaspaas.customer.core.ui.components.LoadingScreen
import com.aaspaas.customer.core.ui.theme.BrandAccent
import com.aaspaas.customer.domain.model.Product
import com.aaspaas.customer.domain.model.Store
import com.aaspaas.customer.feature.search.viewmodel.SearchViewModel

@Composable
fun SearchScreen(
    initialQuery: String,
    onProductClick: (String, String) -> Unit,
    onStoreClick: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val query by viewModel.query.collectAsStateWithLifecycle()
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        if (initialQuery.isNotEmpty()) viewModel.onQueryChange(initialQuery)
        focusRequester.requestFocus()
    }

    Column(Modifier.fillMaxSize()) {
        // Search bar
        Row(
            modifier = Modifier.fillMaxWidth().padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, contentDescription = "Back") }
            OutlinedTextField(
                value = query,
                onValueChange = { viewModel.onQueryChange(it) },
                modifier = Modifier.weight(1f).focusRequester(focusRequester),
                placeholder = { Text("Search products near you...") },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { viewModel.search() }),
                trailingIcon = {
                    if (query.isNotEmpty()) {
                        IconButton(onClick = { viewModel.onQueryChange("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                }
            )
        }

        when {
            state.isLoading && !state.hasSearched -> Column(Modifier.fillMaxSize()) {
                Spacer(Modifier.height(8.dp))
                ShimmerItem(modifier = Modifier.height(48.dp).fillMaxWidth().padding(horizontal = 16.dp), shape = RoundedCornerShape(12.dp))
                Spacer(Modifier.height(16.dp))
                repeat(3) {
                    Column(Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                        ShimmerItem(modifier = Modifier.height(64.dp).fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                        Spacer(Modifier.height(8.dp))
                    }
                }
            }
            state.error != null -> ErrorScreen(state.error!!, onRetry = { viewModel.search() })
            !state.hasSearched -> EmptyScreen("Search for products, brands, or categories")
            state.products.isEmpty() && state.stores.isEmpty() -> EmptyScreen("No results found for \"$query\"")
            else -> SearchResults(
                products = state.products,
                stores = state.stores,
                onProductClick = onProductClick,
                onStoreClick = onStoreClick
            )
        }
    }
}

@Composable
private fun SearchResults(
    products: List<Product>,
    stores: List<Store>,
    onProductClick: (String, String) -> Unit,
    onStoreClick: (String) -> Unit
) {
    LazyColumn(contentPadding = PaddingValues(bottom = 16.dp)) {
        if (products.isNotEmpty()) {
            item {
                Text(
                    "Products (${products.size})",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            items(products) { product ->
                ProductResultCard(product = product, onClick = {
                    val variantId = product.variants.firstOrNull()?.id ?: return@ProductResultCard
                    onProductClick(product.id, variantId)
                })
            }
        }
        if (stores.isNotEmpty()) {
            item {
                Text(
                    "Stores (${stores.size})",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            items(stores) { store ->
                StoreResultCard(store = store, onClick = { onStoreClick(store.id) })
            }
        }
    }
}

@Composable
private fun ProductResultCard(product: Product, onClick: () -> Unit) {
    val variant = product.variants.firstOrNull()
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp).clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(64.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.ShoppingBag, contentDescription = null, modifier = Modifier.size(32.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(product.name, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                product.brand?.let { Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                Text(product.store.name, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                product.store.distanceKm?.let {
                    Text("${String.format("%.1f", it)} km away", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                if (variant != null) {
                    Text("₹${variant.price.toInt()}", style = MaterialTheme.typography.titleMedium, color = BrandAccent)
                }
                Text(
                    text = if (variant?.inventory?.availableStock ?: 0 > 0) "Available" else "Out of Stock",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (variant?.inventory?.availableStock ?: 0 > 0) com.aaspaas.customer.core.ui.theme.Available else com.aaspaas.customer.core.ui.theme.OutOfStock
                )
            }
        }
    }
}

@Composable
private fun StoreResultCard(store: Store, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp).clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Store, contentDescription = null, modifier = Modifier.size(40.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(store.name, style = MaterialTheme.typography.titleMedium)
                Text(store.address, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
                store.distanceKm?.let { Text("${String.format("%.1f", it)} km", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
            }
            Text(
                text = if (store.isOpen) "Open" else "Closed",
                style = MaterialTheme.typography.labelSmall,
                color = if (store.isOpen) com.aaspaas.customer.core.ui.theme.Available else com.aaspaas.customer.core.ui.theme.OutOfStock
            )
        }
    }
}
