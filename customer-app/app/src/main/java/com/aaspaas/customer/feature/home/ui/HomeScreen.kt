package com.aaspaas.customer.feature.home.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aaspaas.customer.core.ui.components.ErrorScreen
import com.aaspaas.customer.core.ui.components.LoadingScreen
import com.aaspaas.customer.core.ui.components.SectionHeader
import com.aaspaas.customer.core.ui.theme.Brand
import com.aaspaas.customer.core.ui.theme.BrandAccent
import com.aaspaas.customer.domain.model.Product
import com.aaspaas.customer.domain.model.Store
import com.aaspaas.customer.feature.home.viewmodel.HomeViewModel

@Composable
fun HomeScreen(
    onSearchClick: (String) -> Unit,
    onProductClick: (String, String) -> Unit,
    onStoreClick: (String) -> Unit,
    onReservationsClick: () -> Unit,
    onProfileClick: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        bottomBar = {
            HomeBottomBar(
                onHomeClick = {},
                onSearchClick = { onSearchClick("") },
                onReservationsClick = onReservationsClick,
                onProfileClick = onProfileClick
            )
        }
    ) { padding ->
        when {
            state.isLoading -> LoadingScreen()
            state.error != null -> ErrorScreen(state.error!!, onRetry = { viewModel.loadHomeData() })
            else -> HomeContent(
                state = state,
                onSearchClick = onSearchClick,
                onProductClick = onProductClick,
                onStoreClick = onStoreClick,
                modifier = Modifier.padding(padding)
            )
        }
    }
}

@Composable
private fun HomeContent(
    state: com.aaspaas.customer.feature.home.viewmodel.HomeUiState,
    onSearchClick: (String) -> Unit,
    onProductClick: (String, String) -> Unit,
    onStoreClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        item { HomeHeader(state.location?.let { "%.4f, %.4f".format(it.latitude, it.longitude) } ?: "Locating...") }
        item { HomeSearchBar(onSearchClick) }
        item { SectionHeader("Nearby Products") }
        item {
            if (state.nearbyProducts.isEmpty()) {
                Text("No products found nearby", modifier = Modifier.padding(16.dp), color = MaterialTheme.colorScheme.onSurfaceVariant)
            } else {
                LazyRow(contentPadding = PaddingValues(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(state.nearbyProducts) { product ->
                        ProductCard(product = product, onClick = {
                            val firstVariant = product.variants.firstOrNull()?.id ?: return@ProductCard
                            onProductClick(product.id, firstVariant)
                        })
                    }
                }
            }
        }
        item { Spacer(Modifier.height(8.dp)) }
        item { SectionHeader("Nearby Stores") }
        items(state.nearbyStores) { store ->
            StoreListItem(store = store, onClick = { onStoreClick(store.id) })
        }
        item { Spacer(Modifier.height(16.dp)) }
    }
}

@Composable
private fun HomeHeader(locationText: String) {
    Row(
        modifier = Modifier.fillMaxWidth().background(Brand).padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.LocationOn, contentDescription = null, tint = BrandAccent, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(4.dp))
        Text(locationText, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
        Text("AAS PAAS WALA", style = MaterialTheme.typography.labelLarge, color = BrandAccent)
    }
}

@Composable
private fun HomeSearchBar(onSearchClick: (String) -> Unit) {
    var query by remember { mutableStateOf("") }
    OutlinedTextField(
        value = query,
        onValueChange = { query = it },
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        placeholder = { Text("Search products near you...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onSearchClick(query) }) {
                    Icon(Icons.Default.ArrowForward, contentDescription = "Search", tint = BrandAccent)
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(12.dp)
    )
}

@Composable
private fun ProductCard(product: Product, onClick: () -> Unit) {
    val variant = product.variants.firstOrNull()
    Card(
        modifier = Modifier.width(180.dp).clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(Modifier.padding(12.dp)) {
            Box(
                modifier = Modifier.fillMaxWidth().height(120.dp).clip(RoundedCornerShape(8.dp)).background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.ShoppingBag, contentDescription = null, modifier = Modifier.size(40.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(Modifier.height(8.dp))
            Text(product.name, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
            product.brand?.let { Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
            if (variant != null) {
                Text("₹${variant.price.toInt()}", style = MaterialTheme.typography.titleMedium, color = BrandAccent)
            }
            Text(product.store.name, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
            product.store.distanceKm?.let {
                Text("${String.format("%.1f", it)} km", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
private fun StoreListItem(store: Store, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp).clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier.size(48.dp).clip(RoundedCornerShape(8.dp)).background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Store, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(store.name, style = MaterialTheme.typography.titleMedium)
                Text(store.address, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    store.distanceKm?.let { Text("${String.format("%.1f", it)} km", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = if (store.isOpen) "Open" else "Closed",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (store.isOpen) com.aaspaas.customer.core.ui.theme.Available else com.aaspaas.customer.core.ui.theme.OutOfStock
                    )
                }
            }
            store.rating?.let {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, contentDescription = null, modifier = Modifier.size(14.dp), tint = com.aaspaas.customer.core.ui.theme.Warning)
                    Text(String.format("%.1f", it), style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}

@Composable
private fun HomeBottomBar(
    onHomeClick: () -> Unit,
    onSearchClick: () -> Unit,
    onReservationsClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    NavigationBar {
        NavigationBarItem(selected = true, onClick = onHomeClick, icon = { Icon(Icons.Default.Home, null) }, label = { Text("Home") })
        NavigationBarItem(selected = false, onClick = onSearchClick, icon = { Icon(Icons.Default.Search, null) }, label = { Text("Search") })
        NavigationBarItem(selected = false, onClick = onReservationsClick, icon = { Icon(Icons.Default.BookmarkBorder, null) }, label = { Text("Reservations") })
        NavigationBarItem(selected = false, onClick = onProfileClick, icon = { Icon(Icons.Default.Person, null) }, label = { Text("Profile") })
    }
}
