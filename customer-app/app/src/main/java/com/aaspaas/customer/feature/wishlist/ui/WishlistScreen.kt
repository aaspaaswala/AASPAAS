package com.aaspaas.customer.feature.wishlist.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items as gridItems
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items as listItems
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aaspaas.customer.core.ui.components.ErrorScreen
import com.aaspaas.customer.core.ui.components.LoadingScreen
import com.aaspaas.customer.core.ui.theme.*
import com.aaspaas.customer.domain.model.Product
import com.aaspaas.customer.domain.model.Store
import com.aaspaas.customer.feature.wishlist.viewmodel.WishlistViewModel

@Composable
fun WishlistScreen(
    onProductClick: (String, String) -> Unit,
    onStoreClick: (String) -> Unit,
    onExplore: () -> Unit,
    onBack: () -> Unit,
    viewModel: WishlistViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Wishlist") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null, tint = TextPrimary) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Surface)
            )
        },
        containerColor = Background
    ) { padding ->
        Column(Modifier.padding(padding)) {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = Surface,
                contentColor = Primary
            ) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Products", style = MaterialTheme.typography.labelLarge) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Stores", style = MaterialTheme.typography.labelLarge) }
                )
            }

            when {
                state.isLoading -> LoadingScreen()
                state.error != null -> {
                    ErrorScreen(state.error!!, onRetry = { viewModel.refresh() })
                }
                selectedTab == 0 -> {
                    if (state.products.isEmpty()) {
                        WishlistEmptyState(
                            emoji = "♡",
                            title = "Your wishlist is empty",
                            subtitle = "Save products you want to check later.",
                            ctaLabel = "Explore Products",
                            onCta = onExplore
                        )
                    } else {
                        WishlistProductGrid(
                            products = state.products,
                            onProductClick = onProductClick,
                            onRemove = { productId -> viewModel.toggleProductWishlist(productId, false) }
                        )
                    }
                }
                selectedTab == 1 -> {
                    if (state.stores.isEmpty()) {
                        WishlistEmptyState(
                            emoji = "🏪",
                            title = "No saved stores",
                            subtitle = "Save stores you visit often.",
                            ctaLabel = "Explore Stores",
                            onCta = onExplore
                        )
                    } else {
                        WishlistStoreList(
                            stores = state.stores,
                            onStoreClick = onStoreClick,
                            onRemove = { storeId -> viewModel.toggleStoreWishlist(storeId, false) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun WishlistEmptyState(
    emoji: String,
    title: String,
    subtitle: String,
    ctaLabel: String,
    onCta: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(emoji, style = MaterialTheme.typography.displayMedium)
        Spacer(Modifier.height(16.dp))
        Text(title, style = MaterialTheme.typography.titleLarge, color = TextPrimary, textAlign = TextAlign.Center)
        Spacer(Modifier.height(8.dp))
        Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = TextSecondary, textAlign = TextAlign.Center)
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = onCta,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Primary)
        ) {
            Text(ctaLabel)
        }
    }
}

@Composable
private fun WishlistProductGrid(
    products: List<Product>,
    onProductClick: (String, String) -> Unit,
    onRemove: (String) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
    gridItems(products) { product ->
            WishlistProductCard(
                product = product,
                onClick = {
                    val variantId = product.variants.firstOrNull()?.id ?: return@WishlistProductCard
                    onProductClick(product.id, variantId)
                },
                onRemove = { onRemove(product.id) }
            )
        }
    }
}

@Composable
private fun WishlistProductCard(
    product: Product,
    onClick: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(1.dp),
        colors = CardDefaults.cardColors(containerColor = Surface)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.ShoppingBag, null, Modifier.size(40.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                IconButton(
                    onClick = onRemove,
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Icon(Icons.Default.Favorite, null, Modifier.size(20.dp), tint = Accent)
                }
            }
            Column(Modifier.padding(12.dp)) {
                Text(product.name, style = MaterialTheme.typography.titleSmall, maxLines = 2, overflow = TextOverflow.Ellipsis, color = TextPrimary)
                Spacer(Modifier.height(4.dp))
                product.variants.firstOrNull()?.let { variant ->
                    Text("₹${variant.price.toInt()}", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                }
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Store, null, Modifier.size(12.dp), tint = Primary)
                    Spacer(Modifier.width(4.dp))
                    Text("${product.variants.size} variants", style = MaterialTheme.typography.labelSmall, color = Primary)
                }
            }
        }
    }
}

@Composable
private fun WishlistStoreList(
    stores: List<Store>,
    onStoreClick: (String) -> Unit,
    onRemove: (String) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        listItems(stores) { store ->
            WishlistStoreCard(
                store = store,
                onClick = { onStoreClick(store.id) },
                onRemove = { onRemove(store.id) }
            )
        }
    }
}

@Composable
private fun WishlistStoreCard(
    store: Store,
    onClick: () -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(1.dp),
        colors = CardDefaults.cardColors(containerColor = Surface)
    ) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Primary.copy(alpha = 0.08f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.Store, null, Modifier.size(28.dp), tint = Primary)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(store.name, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                Spacer(Modifier.height(4.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                    store.rating?.let {
                        Icon(Icons.Default.Star, null, Modifier.size(12.dp), tint = Warning)
                        Text(String.format("%.1f", it), style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                        Text("•", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                    }
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
            IconButton(onClick = onRemove) {
                Icon(Icons.Default.Favorite, null, Modifier.size(20.dp), tint = Accent)
            }
        }
    }
}
