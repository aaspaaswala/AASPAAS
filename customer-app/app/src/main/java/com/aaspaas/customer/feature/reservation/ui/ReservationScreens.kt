package com.aaspaas.customer.feature.reservation.ui

import android.content.Intent
import android.net.Uri
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aaspaas.customer.core.ui.components.ErrorScreen
import com.aaspaas.customer.core.ui.components.LoadingScreen
import com.aaspaas.customer.core.ui.theme.*
import com.aaspaas.customer.domain.model.Reservation
import com.aaspaas.customer.domain.model.ReservationStatus
import com.aaspaas.customer.feature.reservation.viewmodel.*

// ── Confirm Screen ────────────────────────────────────────────────────────────

@Composable
fun ReservationConfirmScreen(
    variantId: String,
    onConfirmed: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: ReservationConfirmViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(variantId) { viewModel.load(variantId) }
    LaunchedEffect(state.confirmedReservationId) {
        state.confirmedReservationId?.let { onConfirmed(it) }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reserve Product") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) } }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(padding).padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(Icons.Default.BookmarkAdd, contentDescription = null, modifier = Modifier.size(64.dp), tint = BrandAccent)
            Spacer(Modifier.height(24.dp))
            Text("Reserve for 6 Hours", style = MaterialTheme.typography.headlineMedium, textAlign = TextAlign.Center)
            Spacer(Modifier.height(8.dp))
            Text(
                "The product will be held for you at the store for 6 hours. Visit the store to complete your purchase.",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            state.error?.let {
                Spacer(Modifier.height(12.dp))
                Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center)
            }

            Spacer(Modifier.height(32.dp))

            Button(
                onClick = { viewModel.confirmReservation(variantId) },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                enabled = !state.isLoading,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BrandAccent)
            ) {
                if (state.isLoading) CircularProgressIndicator(Modifier.size(20.dp), color = MaterialTheme.colorScheme.onPrimary, strokeWidth = 2.dp)
                else Text("Confirm Reservation", style = MaterialTheme.typography.labelLarge)
            }

            Spacer(Modifier.height(12.dp))
            OutlinedButton(onClick = onBack, modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(12.dp)) {
                Text("Cancel")
            }
        }
    }
}

// ── Detail / Countdown Screen ─────────────────────────────────────────────────

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
                        title = { Text("Reservation") },
                        navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) } }
                    )
                }
            ) { padding ->
                LazyColumn(Modifier.padding(padding).padding(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    item { ReservationStatusBadge(reservation.status) }
                    item { CountdownCard(state.remainingSeconds, reservation) }
                    item { ReservationProductCard(reservation) }
                    item { ReservationStoreCard(reservation, context) }
                    if (reservation.status in listOf(ReservationStatus.ACTIVE, ReservationStatus.CONFIRMED, ReservationStatus.PENDING)) {
                        item {
                            OutlinedButton(
                                onClick = { viewModel.cancelReservation(reservationId) },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                            ) {
                                Text("Cancel Reservation")
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
        ReservationStatus.ACTIVE, ReservationStatus.CONFIRMED -> StateActive to "Active"
        ReservationStatus.PENDING -> StatePending to "Pending"
        ReservationStatus.COMPLETED -> StateCompleted to "Completed"
        ReservationStatus.CANCELLED -> StateCancelled to "Cancelled"
        ReservationStatus.EXPIRED -> StateExpired to "Expired"
    }
    Surface(shape = RoundedCornerShape(20.dp), color = color.copy(alpha = 0.12f)) {
        Text(
            text = "● $label",
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
        colors = CardDefaults.cardColors(containerColor = Brand)
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (isActive && remainingSeconds > 0) {
                val hours = remainingSeconds / 3600
                val minutes = (remainingSeconds % 3600) / 60
                val seconds = remainingSeconds % 60
                Text("Time Remaining", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f))
                Spacer(Modifier.height(8.dp))
                Text(
                    text = "%02d:%02d:%02d".format(hours, minutes, seconds),
                    style = MaterialTheme.typography.displayMedium,
                    color = BrandAccent
                )
                Spacer(Modifier.height(4.dp))
                Text("Expires at ${reservation.expiresAt}", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.5f))
            } else {
                Text(
                    text = if (reservation.status == ReservationStatus.COMPLETED) "Purchase Completed" else "Reservation Ended",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

@Composable
private fun ReservationProductCard(reservation: Reservation) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), elevation = CardDefaults.cardElevation(1.dp)) {
        Column(Modifier.padding(16.dp)) {
            Text("Product", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(4.dp))
            Text(reservation.product.name, style = MaterialTheme.typography.titleLarge)
            reservation.product.brand?.let { Text(it, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                reservation.variant.size?.let { VariantChip("Size: $it") }
                reservation.variant.color?.let { VariantChip("Color: $it") }
            }
            Spacer(Modifier.height(8.dp))
            Text("₹${reservation.variant.price.toInt()}", style = MaterialTheme.typography.headlineSmall, color = BrandAccent)
        }
    }
}

@Composable
private fun VariantChip(label: String) {
    Surface(shape = RoundedCornerShape(8.dp), color = MaterialTheme.colorScheme.surfaceVariant) {
        Text(label, modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
private fun ReservationStoreCard(reservation: Reservation, context: android.content.Context) {
    val store = reservation.store
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), elevation = CardDefaults.cardElevation(1.dp)) {
        Column(Modifier.padding(16.dp)) {
            Text("Store", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(4.dp))
            Text(store.name, style = MaterialTheme.typography.titleLarge)
            Text(store.address, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                store.phone?.let { phone ->
                    OutlinedButton(onClick = { context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone"))) }, modifier = Modifier.weight(1f)) {
                        Icon(Icons.Default.Phone, null, Modifier.size(16.dp))
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
    val tabs = listOf("Active", "Completed", "Cancelled", "Expired")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Reservations") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) } }
            )
        }
    ) { padding ->
        Column(Modifier.padding(padding)) {
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(selected = selectedTab == index, onClick = { selectedTab = index }, text = { Text(title) })
                }
            }

            val list = when (selectedTab) {
                0 -> state.active
                1 -> state.completed
                2 -> state.cancelled
                else -> state.expired
            }

            if (list.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No ${tabs[selectedTab].lowercase()} reservations", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(1.dp),
        onClick = onClick
    ) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(reservation.product.name, style = MaterialTheme.typography.titleMedium)
                Text(reservation.store.name, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("₹${reservation.variant.price.toInt()}", style = MaterialTheme.typography.titleSmall, color = BrandAccent)
            }
            ReservationStatusBadge(reservation.status)
        }
    }
}
