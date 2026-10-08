package com.aaspaas.customer.feature.reservation.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aaspaas.customer.core.ui.components.EmptyScreen
import com.aaspaas.customer.core.ui.components.ErrorScreen
import com.aaspaas.customer.core.ui.components.LoadingScreen
import com.aaspaas.customer.core.ui.components.ReservationCardShimmer
import com.aaspaas.customer.core.ui.theme.*
import com.aaspaas.customer.domain.model.Product
import com.aaspaas.customer.domain.model.ProductVariant
import com.aaspaas.customer.domain.model.Reservation
import com.aaspaas.customer.domain.model.ReservationStatus
import com.aaspaas.customer.feature.reservation.viewmodel.*

// ── Reservation Confirm Screen ────────────────────────────────────────────────

@Composable
fun ReservationConfirmScreen(
    productId: String,
    variantId: String,
    onConfirmed: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: ReservationConfirmViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var quantity by remember { mutableIntStateOf(1) }
    var selectedDuration by remember { mutableIntStateOf(0) } // 0=6h, 1=24h, 2=48h

    LaunchedEffect(productId, variantId) { viewModel.load(productId, variantId) }
    LaunchedEffect(state.confirmedReservationId) {
        state.confirmedReservationId?.let { onConfirmed(it) }
    }

    when {
        state.isLoading -> {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Reserve Product") },
                        navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) } },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = Surface)
                    )
                },
                containerColor = Background
            ) { padding ->
                Column(Modifier.padding(padding).fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    CircularProgressIndicator(color = Primary)
                }
            }
        }
        state.error != null -> {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Reserve Product") },
                        navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) } },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = Surface)
                    )
                },
                containerColor = Background
            ) { padding ->
                Column(Modifier.padding(padding).padding(32.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                    Text(state.error!!, color = Error, style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.Center)
                }
            }
        }
        state.product != null && state.selectedVariant != null -> {
            val product = state.product!!
            val currentVariant = state.selectedVariant!!
            ReservationConfirmContent(
                product = product,
                variant = currentVariant,
                quantity = quantity,
                selectedDuration = selectedDuration,
                onQuantityChange = { quantity = it },
                onDurationChange = { selectedDuration = it },
                onConfirm = { viewModel.confirmReservation(variantId) },
                onBack = onBack,
                state = state
            )
        }
    }
}

@Composable
private fun QuantityButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (enabled) Primary.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surfaceVariant,
        modifier = Modifier.size(36.dp)
    ) {
        IconButton(onClick = onClick, enabled = enabled) {
            Icon(icon, null, Modifier.size(18.dp), tint = if (enabled) Primary else TextSecondary)
        }
    }
}

@Composable
private fun DurationOption(
    label: String,
    sublabel: String,
    isSelected: Boolean,
    isPro: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .border(1.dp, if (isSelected) Primary else Border, RoundedCornerShape(10.dp))
            .background(if (isSelected) Primary.copy(alpha = 0.05f) else Surface)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        RadioButton(
            selected = isSelected,
            onClick = onClick,
            colors = RadioButtonDefaults.colors(selectedColor = Primary)
        )
        Spacer(Modifier.width(8.dp))
        Column(Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.titleSmall, color = TextPrimary)
            Text(sublabel, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
        }
        if (isPro) {
            Surface(shape = RoundedCornerShape(4.dp), color = Accent.copy(alpha = 0.12f)) {
                Text(
                    "PRO",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = Accent
                )
            }
        }
    }
}

// ── Reservation Confirmation Content ────────────────────────────────────────────

@Composable
private fun ReservationConfirmContent(
    product: Product,
    variant: ProductVariant,
    quantity: Int,
    selectedDuration: Int,
    onQuantityChange: (Int) -> Unit,
    onDurationChange: (Int) -> Unit,
    onConfirm: () -> Unit,
    onBack: () -> Unit,
    state: ReservationConfirmUiState
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reserve Product") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Surface)
            )
        },
        containerColor = Background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Product summary card
                item {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = Surface,
                        tonalElevation = 1.dp
                    ) {
                        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                Modifier
                                    .size(64.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.ShoppingBag, null, Modifier.size(32.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Spacer(Modifier.width(12.dp))
                            Column(Modifier.weight(1f)) {
                                Text(product.name, style = MaterialTheme.typography.titleMedium, color = TextPrimary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Text(product.brand ?: product.store.name, style = MaterialTheme.typography.bodySmall, color = TextSecondary, maxLines = 1, overflow = TextOverflow.Ellipsis)
                            }
                            Text("₹${variant.price.toInt()}", style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                        }
                    }
                }

                // Quantity selector
                item {
                    Surface(shape = RoundedCornerShape(16.dp), color = Surface, tonalElevation = 1.dp) {
                        Column(Modifier.padding(16.dp)) {
                            Text("Quantity", style = MaterialTheme.typography.titleSmall, color = TextPrimary)
                            Spacer(Modifier.height(12.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                QuantityButton(icon = Icons.Default.Remove, enabled = quantity > 1) {
                                    onQuantityChange(quantity - 1)
                                }
                                Spacer(Modifier.width(20.dp))
                                Text(
                                    quantity.toString(),
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = TextPrimary
                                )
                                Spacer(Modifier.width(20.dp))
                                QuantityButton(icon = Icons.Default.Add, enabled = quantity < 5) {
                                    onQuantityChange(quantity + 1)
                                }
                            }
                        }
                    }
                }

                // Reservation duration
                item {
                    Surface(shape = RoundedCornerShape(16.dp), color = Surface, tonalElevation = 1.dp) {
                        Column(Modifier.padding(16.dp)) {
                            Text("Reservation period", style = MaterialTheme.typography.titleSmall, color = TextPrimary)
                            Spacer(Modifier.height(12.dp))
                            DurationOption(
                                label = "6 Hours",
                                sublabel = "Free",
                                isSelected = selectedDuration == 0,
                                isPro = false,
                                onClick = { onDurationChange(0) }
                            )
                            Spacer(Modifier.height(8.dp))
                            DurationOption(
                                label = "24 Hours",
                                sublabel = "AasPaasWala Plus",
                                isSelected = selectedDuration == 1,
                                isPro = true,
                                onClick = { onDurationChange(1) }
                            )
                            Spacer(Modifier.height(8.dp))
                            DurationOption(
                                label = "48 Hours",
                                sublabel = "AasPaasWala Plus",
                                isSelected = selectedDuration == 2,
                                isPro = true,
                                onClick = { onDurationChange(2) }
                            )
                        }
                    }
                }

                state.error?.let {
                    item {
                        Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                    }
                }
            }

            // Bottom CTA
            Surface(shadowElevation = 8.dp, color = Surface) {
                Button(
                    onClick = onConfirm,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                        .height(52.dp),
                    enabled = !state.isLoading,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary)
                ) {
                    if (state.isLoading) {
                        CircularProgressIndicator(Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                    } else {
                        Text("Confirm Reservation", style = MaterialTheme.typography.labelLarge)
                    }
                }
            }
        }
    }
}

// ── Reservation Confirmation Success Screen ───────────────────────────────────

@Composable
fun ReservationSuccessScreen(
    reservationId: String,
    productName: String,
    storeName: String,
    onViewReservation: () -> Unit,
    onGetDirections: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            shape = RoundedCornerShape(50.dp),
            color = Success.copy(alpha = 0.12f),
            modifier = Modifier.size(80.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(Icons.Default.Check, null, Modifier.size(40.dp), tint = Success)
            }
        }
        Spacer(Modifier.height(24.dp))
        Text("Reservation confirmed", style = MaterialTheme.typography.headlineMedium, color = TextPrimary, textAlign = TextAlign.Center)
        Spacer(Modifier.height(8.dp))
        Text(
            "$productName is reserved at $storeName",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        Surface(shape = RoundedCornerShape(8.dp), color = Warning.copy(alpha = 0.1f)) {
            Text(
                "Reserved until Today, 8:30 PM",
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                style = MaterialTheme.typography.labelMedium,
                color = Warning
            )
        }
        Spacer(Modifier.height(32.dp))
        Button(
            onClick = onViewReservation,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Primary)
        ) {
            Text("View Reservation", style = MaterialTheme.typography.labelLarge)
        }
        Spacer(Modifier.height(12.dp))
        OutlinedButton(
            onClick = onGetDirections,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Border)
        ) {
            Icon(Icons.Default.Directions, null, Modifier.size(18.dp), tint = TextPrimary)
            Spacer(Modifier.width(8.dp))
            Text("Get Directions", style = MaterialTheme.typography.labelLarge, color = TextPrimary)
        }
    }
}

// ── Reservation Detail Screen ─────────────────────────────────────────────────

@Composable
fun ReservationDetailScreen(
    reservationId: String,
    onBack: () -> Unit,
    viewModel: ReservationDetailViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(reservationId) { viewModel.load(reservationId) }

    when {
        state.isLoading -> LoadingScreen()
        state.error != null -> ErrorScreen(state.error!!, onRetry = { viewModel.load(reservationId) })
        state.reservation != null -> {
            val reservation = state.reservation!!
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Reservation #${reservation.id.takeLast(6).uppercase()}") },
                        navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) } },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = Surface)
                    )
                },
                containerColor = Background
            ) { padding ->
                LazyColumn(
                    Modifier.padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item { ReservationStatusBadge(reservation.status) }
                    item { CountdownCard(state.remainingSeconds, reservation) }
                    item { ReservationProductCard(reservation) }
                    item { ReservationStoreCard(reservation, context) }
                    if (reservation.status in listOf(ReservationStatus.ACTIVE, ReservationStatus.CONFIRMED, ReservationStatus.PENDING)) {
                        item {
                            OutlinedButton(
                                onClick = { viewModel.cancelReservation(reservationId) },
                                modifier = Modifier.fillMaxWidth().height(48.dp),
                                shape = RoundedCornerShape(12.dp),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Error.copy(alpha = 0.4f)),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Error)
                            ) {
                                Text("Cancel Reservation", style = MaterialTheme.typography.labelLarge)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ReservationStatusBadge(status: ReservationStatus) {
    val (color, label) = when (status) {
        ReservationStatus.ACTIVE, ReservationStatus.CONFIRMED -> Success to "● RESERVED"
        ReservationStatus.PENDING -> Warning to "● PENDING"
        ReservationStatus.COMPLETED -> Primary to "● COMPLETED"
        ReservationStatus.CANCELLED -> Error to "● CANCELLED"
        ReservationStatus.EXPIRED -> TextSecondary to "● EXPIRED"
    }
    Surface(shape = RoundedCornerShape(20.dp), color = color.copy(alpha = 0.1f)) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelLarge,
            color = color
        )
    }
}

@Composable
private fun CountdownCard(remainingSeconds: Long, reservation: Reservation) {
    val isActive = reservation.status in listOf(ReservationStatus.ACTIVE, ReservationStatus.CONFIRMED, ReservationStatus.PENDING)
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Primary)
    ) {
        Column(
            modifier = Modifier.padding(24.dp).fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isActive && remainingSeconds > 0) {
                val hours = remainingSeconds / 3600
                val minutes = (remainingSeconds % 3600) / 60
                val seconds = remainingSeconds % 60
                Text(
                    "Reservation expires in",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.7f)
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "%02d:%02d:%02d".format(hours, minutes, seconds),
                    style = MaterialTheme.typography.displayMedium,
                    color = Accent
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "Expires at ${reservation.expiresAt}",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.5f)
                )
            } else {
                Text(
                    text = if (reservation.status == ReservationStatus.COMPLETED) "Purchase Completed ✓" else "Reservation Ended",
                    style = MaterialTheme.typography.headlineSmall,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun ReservationProductCard(reservation: Reservation) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(1.dp),
        colors = CardDefaults.cardColors(containerColor = Surface)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Product", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
            Spacer(Modifier.height(8.dp))
            Text(reservation.product.name, style = MaterialTheme.typography.titleLarge, color = TextPrimary)
            reservation.product.brand?.let {
                Text(it, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                reservation.variant.size?.let { VariantTag("Size: $it") }
                reservation.variant.color?.let { VariantTag("Color: $it") }
                VariantTag("Qty: 1")
            }
            Spacer(Modifier.height(8.dp))
            Text("₹${reservation.variant.price.toInt()}", style = MaterialTheme.typography.headlineSmall, color = TextPrimary)
        }
    }
}

@Composable
private fun VariantTag(label: String) {
    Surface(shape = RoundedCornerShape(6.dp), color = MaterialTheme.colorScheme.surfaceVariant) {
        Text(label, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall, color = TextSecondary)
    }
}

@Composable
private fun ReservationStoreCard(reservation: Reservation, context: android.content.Context) {
    val store = reservation.store
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(1.dp),
        colors = CardDefaults.cardColors(containerColor = Surface)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text("Store", style = MaterialTheme.typography.labelMedium, color = TextSecondary)
            Spacer(Modifier.height(8.dp))
            Text(store.name, style = MaterialTheme.typography.titleLarge, color = TextPrimary)
            Text(store.address, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
            store.distanceKm?.let {
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LocationOn, null, Modifier.size(14.dp), tint = TextSecondary)
                    Spacer(Modifier.width(2.dp))
                    Text("${String.format("%.1f", it)} km away", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
                }
            }
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                store.phone?.let { phone ->
                    OutlinedButton(
                        onClick = { context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Border)
                    ) {
                        Icon(Icons.Default.Phone, null, Modifier.size(16.dp), tint = TextPrimary)
                        Spacer(Modifier.width(4.dp))
                        Text("Call", color = TextPrimary)
                    }
                }
                Button(
                    onClick = {
                        val uri = Uri.parse("geo:${store.latitude},${store.longitude}?q=${store.latitude},${store.longitude}(${store.name})")
                        context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                    },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Primary)
                ) {
                    Icon(Icons.Default.Directions, null, Modifier.size(16.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("Directions")
                }
            }
        }
    }
}

// ── My Reservations Screen ────────────────────────────────────────────────────

@Composable
fun MyReservationsScreen(
    onReservationClick: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: MyReservationsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Active", "Completed", "Cancelled")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Reservations") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) } },
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
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title, style = MaterialTheme.typography.labelLarge) }
                    )
                }
            }

            val list = when (selectedTab) {
                0 -> state.active
                1 -> state.completed
                else -> state.cancelled
            }

            when {
                state.isLoading -> LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(3) { ReservationCardShimmer() }
                }
                list.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
                        Text("📦", style = MaterialTheme.typography.displayMedium)
                        Spacer(Modifier.height(16.dp))
                        Text(
                            "No ${tabs[selectedTab].lowercase()} reservations",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            "Reserve a product from a nearby store and it'll appear here.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
                else -> LazyColumn(
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(list) { reservation ->
                        ReservationListCard(reservation = reservation, onClick = { onReservationClick(reservation.id) })
                    }
                }
            }
        }
    }
}

@Composable
private fun ReservationListCard(reservation: Reservation, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(1.dp),
        colors = CardDefaults.cardColors(containerColor = Surface),
        onClick = onClick
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(reservation.product.name, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                Spacer(Modifier.height(2.dp))
                Text(reservation.store.name, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                Spacer(Modifier.height(4.dp))
                Text("₹${reservation.variant.price.toInt()}", style = MaterialTheme.typography.titleSmall, color = TextPrimary)
            }
            Column(horizontalAlignment = Alignment.End) {
                ReservationStatusBadge(reservation.status)
                Spacer(Modifier.height(8.dp))
                Text("View Details →", style = MaterialTheme.typography.labelSmall, color = Primary)
            }
        }
    }
}
