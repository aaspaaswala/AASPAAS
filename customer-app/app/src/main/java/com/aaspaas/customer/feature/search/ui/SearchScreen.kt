package com.aaspaas.customer.feature.search.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import com.aaspaas.customer.core.ui.components.ErrorScreen
import com.aaspaas.customer.core.ui.components.NoResultsScreen
import com.aaspaas.customer.core.ui.components.ShimmerItem
import com.aaspaas.customer.core.ui.theme.*
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

    Column(
        Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        // Search bar header
        Surface(color = Surface, shadowElevation = 1.dp) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                }
                OutlinedTextField(
                    value = query,
                    onValueChange = { viewModel.onQueryChange(it) },
                    modifier = Modifier
                        .weight(1f)
                        .focusRequester(focusRequester),
                    placeholder = {
                        Text("Search products, brands or stores", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { viewModel.search() }),
                    leadingIcon = {
                        Icon(Icons.Default.Search, null, tint = TextSecondary, modifier = Modifier.size(20.dp))
                    },
                    trailingIcon = {
                        if (query.isNotEmpty()) {
                            IconButton(onClick = { viewModel.onQueryChange("") }) {
                                Icon(Icons.Default.Clear, null, tint = TextSecondary, modifier = Modifier.size(18.dp))
                            }
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Primary,
                        unfocusedBorderColor = Border
                    )
                )
            }
        }

        when {
            state.isLoading -> SearchShimmer()
            state.error != null -> ErrorScreen(state.error!!, onRetry = { viewModel.search() })
            !state.hasSearched && query.isEmpty() -> RecentSearches(
                recentQueries = state.recentSearches,
                onQueryClick = { viewModel.onQueryChange(it); viewModel.search() },
                onClearAll = { viewModel.clearRecentSearches() }
            )
            !state.hasSearched && query.isNotEmpty() -> SearchSuggestions(
                query = query,
                products = state.products,
                stores = state.stores,
                brands = state.brands,
                categories = state.categories,
                onSuggestionClick = { viewModel.onQueryChange(it); viewModel.search() }
            )
            state.products.isEmpty() && state.stores.isEmpty() -> NoResultsScreen(
                query = query,
                onBrowse = { onBack() }
            )
            else -> SearchResults(
                query = query,
                products = state.products,
                stores = state.stores,
                onProductClick = onProductClick,
                onStoreClick = onStoreClick
            )
        }
    }
}

// ── Recent Searches ───────────────────────────────────────────────────────────

@Composable
private fun RecentSearches(
    recentQueries: List<String>,
    onQueryClick: (String) -> Unit,
    onClearAll: () -> Unit
) {
    LazyColumn(contentPadding = PaddingValues(vertical = 8.dp)) {
        if (recentQueries.isNotEmpty()) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Recent searches",
                        style = MaterialTheme.typography.titleSmall,
                        color = TextPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    TextButton(onClick = onClearAll) {
                        Text("Clear all", style = MaterialTheme.typography.labelMedium, color = Primary)
                    }
                }
            }
            items(recentQueries) { q ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onQueryClick(q) }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.History, null, Modifier.size(18.dp), tint = TextSecondary)
                    Spacer(Modifier.width(12.dp))
                    Text(q, style = MaterialTheme.typography.bodyMedium, color = TextPrimary, modifier = Modifier.weight(1f))
                    Icon(Icons.Default.NorthWest, null, Modifier.size(16.dp), tint = TextSecondary)
                }
                HorizontalDivider(color = Border, modifier = Modifier.padding(horizontal = 16.dp))
            }
        } else {
            item {
                Box(Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                    Text("Search for products, brands or categories", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                }
            }
        }
    }
}

// ── Search Suggestions ────────────────────────────────────────────────────────

@Composable
private fun SearchSuggestions(
    query: String,
    products: List<Product>,
    stores: List<Store>,
    brands: List<String>,
    categories: List<String>,
    onSuggestionClick: (String) -> Unit
) {
    LazyColumn(contentPadding = PaddingValues(vertical = 8.dp)) {
        if (products.isNotEmpty()) {
            item { Text("Products", style = MaterialTheme.typography.titleSmall, color = TextPrimary, modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) }
            items(products.take(5)) { product ->
                SuggestionItem(
                    icon = Icons.Default.ShoppingBag,
                    label = product.name,
                    subtitle = product.brand,
                    onClick = { onSuggestionClick(product.name) }
                )
            }
        }
        if (brands.isNotEmpty()) {
            item { Text("Brands", style = MaterialTheme.typography.titleSmall, color = TextPrimary, modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) }
            items(brands.take(5)) { brand ->
                SuggestionItem(
                    icon = Icons.Default.Search,
                    label = brand,
                    subtitle = null,
                    onClick = { onSuggestionClick(brand) }
                )
            }
        }
        if (stores.isNotEmpty()) {
            item { Text("Stores", style = MaterialTheme.typography.titleSmall, color = TextPrimary, modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) }
            items(stores.take(5)) { store ->
                SuggestionItem(
                    icon = Icons.Default.Store,
                    label = store.name,
                    subtitle = store.address,
                    onClick = { onSuggestionClick(store.name) }
                )
            }
        }
        if (categories.isNotEmpty()) {
            item { Text("Categories", style = MaterialTheme.typography.titleSmall, color = TextPrimary, modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) }
            items(categories.take(5)) { category ->
                SuggestionItem(
                    icon = Icons.Default.Category,
                    label = category,
                    subtitle = null,
                    onClick = { onSuggestionClick(category) }
                )
            }
        }
        if (products.isEmpty() && stores.isEmpty() && brands.isEmpty() && categories.isEmpty()) {
            item {
                SuggestionItem(
                    icon = Icons.Default.Search,
                    label = "$query (suggestion)",
                    subtitle = null,
                    onClick = { onSuggestionClick(query) }
                )
            }
        }
    }
}

@Composable
private fun SuggestionItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, subtitle: String?, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, Modifier.size(18.dp), tint = TextSecondary)
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
            subtitle?.let {
                Text(it, style = MaterialTheme.typography.bodySmall, color = TextSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
        Icon(Icons.Default.NorthWest, null, Modifier.size(16.dp), tint = TextSecondary)
    }
}

// ── Search Results ────────────────────────────────────────────────────────────

@Composable
private fun SearchResults(
    query: String,
    products: List<Product>,
    stores: List<Store>,
    onProductClick: (String, String) -> Unit,
    onStoreClick: (String) -> Unit
) {
    LazyColumn(contentPadding = PaddingValues(bottom = 16.dp)) {
        // Results count header
        item {
            Text(
                "${products.size + stores.size} results for \"$query\"",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )
        }

        if (products.isNotEmpty()) {
            item {
                Text(
                    "Products (${products.size})",
                    style = MaterialTheme.typography.titleSmall,
                    color = TextPrimary,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            items(products.chunked(2)) { rowItems ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    rowItems.forEach { product ->
                        ProductResultCard(
                            modifier = Modifier
                                .weight(1f)
                                .height(180.dp),
                            product = product,
                            onClick = {
                                val variantId = product.variants.firstOrNull()?.id ?: return@ProductResultCard
                                onProductClick(product.id, variantId)
                            }
                        )
                    }
                    if (rowItems.size == 1) {
                        Spacer(Modifier.weight(1f))
                    }
                }
            }
        }

        if (stores.isNotEmpty()) {
            item {
                Spacer(Modifier.height(8.dp))
                Text(
                    "Stores (${stores.size})",
                    style = MaterialTheme.typography.titleSmall,
                    color = TextPrimary,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            items(stores) { store ->
                StoreResultCard(store = store, onClick = { onStoreClick(store.id) })
            }
        }
    }
}

// ── Product Result Card ───────────────────────────────────────────────────────

@Composable
private fun ProductResultCard(
    product: Product,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val variant = product.variants.firstOrNull()
    val isAvailable = (variant?.inventory?.availableStock ?: 0) > 0

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(1.dp),
        colors = CardDefaults.cardColors(containerColor = Surface)
    ) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.ShoppingBag, null, Modifier.size(28.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(product.name, style = MaterialTheme.typography.titleSmall, maxLines = 1, overflow = TextOverflow.Ellipsis, color = TextPrimary)
                product.brand?.let {
                    Text(it, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                }
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Icon(Icons.Default.LocationOn, null, Modifier.size(12.dp), tint = TextSecondary)
                    product.store.distanceKm?.let {
                        Text("${String.format("%.1f", it)} km", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                    }
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                variant?.let {
                    Text("₹${it.price.toInt()}", style = MaterialTheme.typography.titleSmall, color = TextPrimary)
                }
                Spacer(Modifier.height(4.dp))
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = if (isAvailable) Success.copy(alpha = 0.1f) else Error.copy(alpha = 0.1f)
                ) {
                    Text(
                        if (isAvailable) "Available" else "Out of stock",
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isAvailable) Success else Error
                    )
                }
            }
        }
    }
}

// ── Store Result Card ─────────────────────────────────────────────────────────

@Composable
private fun StoreResultCard(store: Store, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(1.dp),
        colors = CardDefaults.cardColors(containerColor = Surface)
    ) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(Primary.copy(alpha = 0.08f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Store, null, Modifier.size(24.dp), tint = Primary)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(store.name, style = MaterialTheme.typography.titleSmall, color = TextPrimary)
                Text(store.address, style = MaterialTheme.typography.bodySmall, color = TextSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    store.distanceKm?.let {
                        Text("${String.format("%.1f", it)} km", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                        Text("•", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                    }
                    Text(
                        if (store.isOpen) "Open" else "Closed",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (store.isOpen) Success else Error
                    )
                }
            }
            store.rating?.let {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, null, Modifier.size(12.dp), tint = Warning)
                    Spacer(Modifier.width(2.dp))
                    Text(String.format("%.1f", it), style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                }
            }
        }
    }
}

// ── Shimmer ───────────────────────────────────────────────────────────────────

@Composable
private fun SearchShimmer() {
    LazyColumn(contentPadding = PaddingValues(vertical = 8.dp)) {
        items(5) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(1.dp),
                colors = CardDefaults.cardColors(containerColor = Surface)
            ) {
                Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    ShimmerItem(Modifier.size(64.dp), RoundedCornerShape(10.dp))
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        ShimmerItem(Modifier.height(14.dp).fillMaxWidth(0.7f))
                        ShimmerItem(Modifier.height(12.dp).fillMaxWidth(0.5f))
                        ShimmerItem(Modifier.height(12.dp).width(60.dp))
                    }
                    Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        ShimmerItem(Modifier.height(16.dp).width(50.dp))
                        ShimmerItem(Modifier.height(20.dp).width(60.dp), RoundedCornerShape(4.dp))
                    }
                }
            }
        }
    }
}
