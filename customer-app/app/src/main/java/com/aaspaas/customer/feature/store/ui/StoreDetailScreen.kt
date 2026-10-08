package com.aaspaas.customer.feature.store.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aaspaas.customer.core.ui.components.ErrorScreen
import com.aaspaas.customer.core.ui.components.ShimmerItem
import com.aaspaas.customer.core.ui.theme.*
import com.aaspaas.customer.domain.model.Product
import com.aaspaas.customer.domain.model.Store
import com.aaspaas.customer.feature.store.viewmodel.StoreDetailViewModel

@Composable
fun StoreDetailScreen(
    storeId: String,
    onProductClick: (String, String) -> Unit,
    onStoreMapClick: () -> Unit,
    onBack: () -> Unit,
    viewModel: StoreDetailViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(storeId) { viewModel.load(storeId) }

    when {
        state.isLoading -> StoreShimmerScreen(onBack)
        state.error != null -> ErrorScreen(state.error!!, onRetry = { viewModel.load(storeId) })
        state.store != null -> StoreDetailContent(
            store = state.store!!,
            products = state.products,
            onProductClick = onProductClick,
            onStoreMapClick = onStoreMapClick,
            onBack = onBack
        )
    }
}

@Composable
private fun StoreShimmerScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Surface)
            )
        }
    ) { padding ->
        LazyColumn(Modifier.padding(padding).fillMaxSize()) {
            item {
                ShimmerItem(Modifier.fillMaxWidth().height(200.dp), RoundedCornerShape(0.dp))
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    ShimmerItem(Modifier.height(24.dp).fillMaxWidth(0.6f))
                    ShimmerItem(Modifier.height(14.dp).fillMaxWidth(0.8f))
                    ShimmerItem(Modifier.height(14.dp).width(120.dp))
                    Spacer(Modifier.height(4.dp))
                    ShimmerItem(Modifier.height(48.dp).fillMaxWidth(), RoundedCornerShape(12.dp))
                }
                HorizontalDivider(color = Border)
            }
            items(4) {
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ShimmerItem(Modifier.size(56.dp), RoundedCornerShape(10.dp))
                    Spacer(Modifier.width(12.dp))
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        ShimmerItem(Modifier.height(14.dp).fillMaxWidth(0.7f))
                        ShimmerItem(Modifier.height(12.dp).fillMaxWidth(0.5f))
                    }
                    ShimmerItem(Modifier.height(16.dp).width(50.dp))
                }
            }
        }
    }
}

@Composable
private fun StoreDetailContent(
    store: Store,
    products: List<Product>,
    onProductClick: (String, String) -> Unit,
    onStoreMapClick: () -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    var wishlisted by remember { mutableStateOf(false) }
    var productSearch by remember { mutableStateOf("") }

    val filteredProducts = if (productSearch.isBlank()) {
        products
    } else {
        products.filter { p ->
            p.name.contains(productSearch, ignoreCase = true) ||
            p.brand?.contains(productSearch, ignoreCase = true) == true
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {},
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) }
                },
                actions = {
                    IconButton(onClick = { wishlisted = !wishlisted }) {
                        Icon(
                            if (wishlisted) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            null,
                            tint = if (wishlisted) Accent else TextSecondary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Surface)
            )
        },
        containerColor = Background
    ) { padding ->
        LazyColumn(Modifier.padding(padding)) {

            // Store hero image
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(Primary.copy(alpha = 0.08f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Store, null, Modifier.size(64.dp), tint = Primary.copy(alpha = 0.4f))
                }
            }

            // Store info
            item {
                Surface(color = Surface) {
                    Column(Modifier.padding(16.dp)) {
                        Text(store.name, style = MaterialTheme.typography.headlineMedium, color = TextPrimary)
                        Spacer(Modifier.height(6.dp))

                        // Rating + distance + open status
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            store.rating?.let {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Star, null, Modifier.size(14.dp), tint = Warning)
                                    Spacer(Modifier.width(2.dp))
                                    Text(String.format("%.1f", it), style = MaterialTheme.typography.labelMedium, color = TextSecondary)
                                }
                                Text("•", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                            }
                            store.distanceKm?.let {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.LocationOn, null, Modifier.size(14.dp), tint = TextSecondary)
                                    Spacer(Modifier.width(2.dp))
                                    Text("${String.format("%.1f", it)} km", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
                                }
                                Text("•", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                            }
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = if (store.isOpen) Success.copy(alpha = 0.1f) else Error.copy(alpha = 0.1f)
                            ) {
                                Text(
                                    if (store.isOpen) "Open" else "Closed",
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (store.isOpen) Success else Error
                                )
                            }
                        }

                        Spacer(Modifier.height(4.dp))
                        Text(store.address, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)

                        Spacer(Modifier.height(12.dp))

                        // Action buttons — includes View on Map
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            store.phone?.let { phone ->
                                OutlinedButton(
                                    onClick = { context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))) },
                                    modifier = Modifier.weight(1f).height(48.dp),
                                    shape = RoundedCornerShape(10.dp),
                                    border = BorderStroke(1.dp, Border)
                                ) {
                                    Icon(Icons.Default.Phone, null, Modifier.size(16.dp), tint = TextPrimary)
                                    Spacer(Modifier.width(6.dp))
                                    Text("Call", color = TextPrimary)
                                }
                            }
                            Button(
                                onClick = {
                                    val uri = Uri.parse("geo:${store.latitude},${store.longitude}?q=${store.latitude},${store.longitude}(${store.name})")
                                    context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                                },
                                modifier = Modifier.weight(1f).height(48.dp),
                                shape = RoundedCornerShape(10.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Primary)
                            ) {
                                Icon(Icons.Default.Directions, null, Modifier.size(16.dp))
                                Spacer(Modifier.width(6.dp))
                                Text("Directions")
                            }
                            OutlinedButton(
                                onClick = onStoreMapClick,
                                modifier = Modifier.weight(1f).height(48.dp),
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, Border)
                            ) {
                                Icon(Icons.Default.Map, null, Modifier.size(16.dp), tint = TextPrimary)
                                Spacer(Modifier.width(6.dp))
                                Text("Map", color = TextPrimary)
                            }
                        }

                        Spacer(Modifier.height(8.dp))

                        // Weekly hours expandable section
                        ExpandableSection(title = "Weekly Hours") {
                            Text(store.openingHours, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                        }
                    }
                }
            }

            // Divider
            item { HorizontalDivider(color = Border) }

            // Products section header + internal search
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Surface)
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "Products (${filteredProducts.size})",
                        style = MaterialTheme.typography.titleMedium,
                        color = TextPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    if (products.isNotEmpty()) {
                        OutlinedTextField(
                            value = productSearch,
                            onValueChange = { productSearch = it },
                            modifier = Modifier.width(180.dp),
                            placeholder = { Text("Search", style = MaterialTheme.typography.bodySmall, color = TextSecondary) },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Primary,
                                unfocusedBorderColor = Border
                            )
                        )
                    }
                }
            }

            if (filteredProducts.isEmpty()) {
                item {
                    Box(
                        Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No products listed yet", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                    }
                }
            } else {
                items(filteredProducts) { product ->
                    StoreProductItem(product = product, onClick = {
                        val variantId = product.variants.firstOrNull()?.id ?: return@StoreProductItem
                        onProductClick(product.id, variantId)
                    })
                }
            }

            item { Spacer(Modifier.height(16.dp)) }
        }
    }
}

@Composable
private fun ExpandableSection(title: String, content: @Composable () -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(title, style = MaterialTheme.typography.titleMedium, color = TextPrimary, modifier = Modifier.weight(1f))
            Icon(
                if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                null,
                tint = TextSecondary
            )
        }
        if (expanded) {
            Spacer(Modifier.height(8.dp))
            content()
        }
        HorizontalDivider(color = Border, modifier = Modifier.padding(top = 12.dp))
    }
}

@Composable
private fun StoreProductItem(product: Product, onClick: () -> Unit) {
    val variant = product.variants.firstOrNull()
    val isAvailable = (variant?.inventory?.availableStock ?: 0) > 0

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
                    .size(56.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.ShoppingBag, null, Modifier.size(24.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(product.name, style = MaterialTheme.typography.titleSmall, maxLines = 1, overflow = TextOverflow.Ellipsis, color = TextPrimary)
                product.brand?.let {
                    Text(it, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
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
