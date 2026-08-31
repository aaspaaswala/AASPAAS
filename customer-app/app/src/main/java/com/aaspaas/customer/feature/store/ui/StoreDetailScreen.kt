package com.aaspaas.customer.feature.store.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aaspaas.customer.core.ui.components.ErrorScreen
import com.aaspaas.customer.core.ui.components.StoreCardShimmer
import com.aaspaas.customer.core.ui.theme.*
import com.aaspaas.customer.domain.model.Product
import com.aaspaas.customer.domain.model.Store
import com.aaspaas.customer.feature.store.viewmodel.StoreDetailViewModel

@Composable
fun StoreDetailScreen(
    storeId: String,
    onProductClick: (String, String) -> Unit,
    onBack: () -> Unit,
    viewModel: StoreDetailViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(storeId) { viewModel.load(storeId) }

    when {
        state.isLoading -> StoreShimmerScreen()
        state.error != null -> ErrorScreen(state.error!!, onRetry = { viewModel.load(storeId) })
        state.store != null -> StoreDetailContent(
            store = state.store!!,
            products = state.products,
            onProductClick = onProductClick,
            onBack = onBack
        )
    }
}

@Composable
private fun StoreShimmerScreen() {
    LazyColumn(Modifier.fillMaxSize()) {
        item {
            Column(Modifier.padding(16.dp)) {
                ShimmerItem(modifier = Modifier.height(28.dp).fillMaxWidth(), shape = RoundedCornerShape(8.dp))
                Spacer(Modifier.height(8.dp))
                ShimmerItem(modifier = Modifier.height(16.dp).fillMaxWidth(0.8f), shape = RoundedCornerShape(8.dp))
                Spacer(Modifier.height(4.dp))
                ShimmerItem(modifier = Modifier.height(16.dp).fillMaxWidth(0.5f), shape = RoundedCornerShape(8.dp))
                Spacer(Modifier.height(12.dp))
                ShimmerItem(modifier = Modifier.height(48.dp).fillMaxWidth(), shape = RoundedCornerShape(12.dp))
            }
            HorizontalDivider()
        }
        item { SectionHeader("Products") }
        items(3) {
            StoreProductItemShimmer()
        }
        item { Spacer(Modifier.height(80.dp)) }
    }
}

@Composable
private fun StoreProductItemShimmer() {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            ShimmerItem(modifier = Modifier.size(40.dp), shape = RoundedCornerShape(8.dp))
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                ShimmerItem(modifier = Modifier.height(18.dp).fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                ShimmerItem(modifier = Modifier.height(14.dp).fillMaxWidth(0.6f))
            }
            Column(horizontalAlignment = Alignment.End) {
                ShimmerItem(modifier = Modifier.height(18.dp).width(60.dp))
                Spacer(Modifier.height(4.dp))
                ShimmerItem(modifier = Modifier.height(12.dp).width(50.dp))
            }
        }
    }
}

@Composable
private fun StoreDetailContent(
    store: Store,
    products: List<Product>,
    onProductClick: (String, String) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(store.name, maxLines = 1, overflow = TextOverflow.Ellipsis) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) } }
            )
        }
    ) { padding ->
        LazyColumn(Modifier.padding(padding)) {
            item {
                // Store header
                Column(Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(store.name, style = MaterialTheme.typography.headlineMedium)
                            Text(store.address, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Spacer(Modifier.height(4.dp))
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                store.distanceKm?.let {
                                    Text("${String.format("%.1f", it)} km", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("•", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                                Text(
                                    text = if (store.isOpen) "Open Now" else "Closed",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = if (store.isOpen) Available else OutOfStock
                                )
                                Text("•", color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(store.openingHours, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            store.rating?.let {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Star, null, modifier = Modifier.size(16.dp), tint = Warning)
                                    Text(String.format("%.1f", it), style = MaterialTheme.typography.labelMedium)
                                    Text(" (${store.reviewCount} reviews)", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        }
                    }

                    Spacer(Modifier.height(12.dp))

                    // Action buttons
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        store.phone?.let { phone ->
                            OutlinedButton(
                                onClick = {
                                    context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone")))
                                },
                                modifier = Modifier.weight(1f)
                            ) {
                                Icon(Icons.Default.Phone, null, modifier = Modifier.size(16.dp))
                                Spacer(Modifier.width(4.dp))
                                Text("Call")
                            }
                        }
                        Button(
                            onClick = {
                                val uri = Uri.parse("geo:${store.latitude},${store.longitude}?q=${store.latitude},${store.longitude}(${store.name})")
                                context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.buttonColors(containerColor = Brand)
                        ) {
                            Icon(Icons.Default.Directions, null, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(4.dp))
                            Text("Directions")
                        }
                    }
                }
                HorizontalDivider()
            }

            item {
                Text(
                    "Products (${products.size})",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                )
            }

            items(products) { product ->
                StoreProductItem(product = product, onClick = {
                    val variantId = product.variants.firstOrNull()?.id ?: return@StoreProductItem
                    onProductClick(product.id, variantId)
                })
            }

            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}

@Composable
private fun StoreProductItem(product: Product, onClick: () -> Unit) {
    val variant = product.variants.firstOrNull()
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp).clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.ShoppingBag, null, modifier = Modifier.size(40.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(product.name, style = MaterialTheme.typography.titleMedium, maxLines = 1, overflow = TextOverflow.Ellipsis)
                product.brand?.let { Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
            }
            Column(horizontalAlignment = Alignment.End) {
                variant?.let { Text("₹${it.price.toInt()}", style = MaterialTheme.typography.titleMedium, color = BrandAccent) }
                Text(
                    text = if ((variant?.inventory?.availableStock ?: 0) > 0) "Available" else "Out of Stock",
                    style = MaterialTheme.typography.labelSmall,
                    color = if ((variant?.inventory?.availableStock ?: 0) > 0) Available else OutOfStock
                )
            }
        }
    }
}
