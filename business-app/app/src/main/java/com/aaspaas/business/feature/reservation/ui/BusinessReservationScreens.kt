package com.aaspaas.business.feature.reservation.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aaspaas.business.core.ui.components.ErrorScreen
import com.aaspaas.business.core.ui.components.LoadingScreen
import com.aaspaas.business.core.ui.components.ReservationCardShimmer
import com.aaspaas.business.core.ui.components.SectionHeader
import com.aaspaas.business.core.ui.components.ShimmerItem
import com.aaspaas.business.core.ui.theme.*
import com.aaspaas.business.domain.model.Reservation
import com.aaspaas.business.domain.model.ReservationStatus
import com.aaspaas.business.feature.reservation.viewmodel.BusinessReservationDetailViewModel
import com.aaspaas.business.feature.reservation.viewmodel.BusinessReservationsViewModel

@Composable
fun BusinessReservationsScreen(
    onReservationClick: (String) -> Unit,
    onBack: () -> Unit,
    viewModel: BusinessReservationsViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Active", "Completed", "Cancelled", "Expired")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reservations") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) } }
            )
        }
    ) { padding ->
        Column(Modifier.padding(padding)) {
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { i, t -> Tab(selected = selectedTab == i, onClick = { selectedTab = i }, text = { Text(t) }) }
            }
            val list = when (selectedTab) {
                0 -> state.active; 1 -> state.completed; 2 -> state.cancelled; else -> state.expired
            }
            if (state.isLoading) {
                LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(4) { ReservationCardShimmer() }
                }
            } else if (list.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("No ${tabs[selectedTab].lowercase()} reservations", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(list) { reservation ->
                        BusinessReservationCard(
                            reservation = reservation,
                            onClick = { onReservationClick(reservation.id) },
                            onConfirm = { viewModel.confirm(reservation.id) },
                            onComplete = { viewModel.complete(reservation.id) },
                            onCancel = { viewModel.cancel(reservation.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun BusinessReservationCard(
    reservation: Reservation,
    onClick: () -> Unit,
    onConfirm: () -> Unit,
    onComplete: () -> Unit,
    onCancel: () -> Unit
) {
    val isActive = reservation.status in listOf(ReservationStatus.ACTIVE, ReservationStatus.CONFIRMED, ReservationStatus.PENDING)
    Card(shape = RoundedCornerShape(12.dp), elevation = CardDefaults.cardElevation(1.dp), onClick = onClick) {
        Column(Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text(reservation.productName, style = MaterialTheme.typography.titleMedium)
                    Text(reservation.variantDescription, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("₹${reservation.price.toInt()}", style = MaterialTheme.typography.titleSmall, color = BusinessAccent)
                    Text("Customer: ${reservation.customerName}", style = MaterialTheme.typography.bodySmall)
                }
                StatusBadge(reservation.status)
            }
            if (isActive) {
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (reservation.status == ReservationStatus.PENDING) {
                        OutlinedButton(onClick = onConfirm, modifier = Modifier.weight(1f), shape = RoundedCornerShape(8.dp)) {
                            Text("Confirm", style = MaterialTheme.typography.labelSmall)
                        }
                    }
                    Button(
                        onClick = onComplete, modifier = Modifier.weight(1f), shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Available)
                    ) { Text("Mark Sold", style = MaterialTheme.typography.labelSmall) }
                    OutlinedButton(
                        onClick = onCancel, modifier = Modifier.weight(1f), shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                    ) { Text("Cancel", style = MaterialTheme.typography.labelSmall) }
                }
            }
        }
    }
}

@Composable
private fun StatusBadge(status: ReservationStatus) {
    val (color, label) = when (status) {
        ReservationStatus.ACTIVE, ReservationStatus.CONFIRMED -> StateActive to "Active"
        ReservationStatus.PENDING -> StatePending to "Pending"
        ReservationStatus.COMPLETED -> StateCompleted to "Completed"
        ReservationStatus.CANCELLED -> StateCancelled to "Cancelled"
        ReservationStatus.EXPIRED -> StateExpired to "Expired"
    }
    Surface(shape = RoundedCornerShape(20.dp), color = color.copy(alpha = 0.12f)) {
        Text("● $label", modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp), style = MaterialTheme.typography.labelSmall, color = color)
    }
}

@Composable
fun BusinessReservationDetailScreen(reservationId: String, onBack: () -> Unit, viewModel: BusinessReservationDetailViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(reservationId) { viewModel.load(reservationId) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reservation Detail") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) } }
            )
        }
    ) { padding ->
        when {
            state.isLoading -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            state.error != null -> Box(Modifier.fillMaxSize().padding(padding).padding(32.dp), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(state.error!!, color = MaterialTheme.colorScheme.error)
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = { viewModel.load(reservationId) }) { Text("Retry") }
                }
            }
            state.reservation != null -> ReservationDetailContent(reservation = state.reservation!!, modifier = Modifier.padding(padding))
        }
    }
}

@Composable
private fun ReservationDetailContent(reservation: Reservation, modifier: Modifier = Modifier) {
    val isActive = reservation.status in listOf(ReservationStatus.ACTIVE, ReservationStatus.CONFIRMED, ReservationStatus.PENDING)

    LazyColumn(modifier = modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item { StatusBadge(reservation.status) }
        item {
            Card(shape = RoundedCornerShape(16.dp), elevation = CardDefaults.cardElevation(2.dp)) {
                Column(Modifier.padding(16.dp)) {
                    Text("Reservation", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(4.dp))
                    Text(reservation.productName, style = MaterialTheme.typography.headlineSmall)
                    Text(reservation.variantDescription, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(8.dp))
                    Text("₹${reservation.price.toInt()}", style = MaterialTheme.typography.headlineMedium, color = BusinessAccent)
                }
            }
        }
        item {
            Card(shape = RoundedCornerShape(12.dp), elevation = CardDefaults.cardElevation(1.dp)) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    InfoRow("Customer", reservation.customerName)
                    reservation.customerMobile?.let { InfoRow("Mobile", it) }
                    InfoRow("Code", reservation.id.takeLast(8).uppercase())
                    InfoRow("Status", reservation.status.name)
                    InfoRow("Created", reservation.createdAt.toString().take(10))
                    reservation.expiresAt?.let { InfoRow("Expires", it.toString().take(10)) }
                    reservation.completedAt?.let { InfoRow("Completed", it.toString().take(10)) }
                    reservation.cancelledAt?.let { InfoRow("Cancelled", it.toString().take(10)) }
                }
            }
        }
        if (isActive) {
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    if (reservation.status == ReservationStatus.PENDING) {
                        OutlinedButton(onClick = { viewModel.confirm(reservation.id) }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(8.dp)) {
                            Text("Confirm")
                        }
                    }
                    Button(onClick = { viewModel.complete(reservation.id) }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(8.dp), colors = ButtonDefaults.buttonColors(containerColor = Available)) {
                        Text("Mark Sold")
                    }
                    OutlinedButton(onClick = { viewModel.cancel(reservation.id) }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(8.dp), colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)) {
                        Text("Cancel")
                    }
                }
            }
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row {
        Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.width(80.dp))
        Text(value, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
    }
}
