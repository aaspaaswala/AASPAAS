package com.aaspaas.business.feature.dashboard.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.aaspaas.business.core.ui.theme.BusinessBrand
import com.aaspaas.business.core.ui.theme.SurfaceVariant
import com.aaspaas.business.core.ui.theme.White
import com.aaspaas.business.core.ui.components.EmptyState

@Composable
fun BusinessOnboardingScreen(
    onContinue: () -> Unit,
    onBack: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Business onboarding") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) } }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(22.dp),
                color = SurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Set up your store", style = MaterialTheme.typography.headlineSmall, color = BusinessBrand)
                    Text(
                        "Complete a few essentials to get started and start serving customers faster.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            listOf(
                "Add store details and operating hours",
                "Create your first product catalog",
                "Set reservation and stock preferences",
                "Review analytics and customer insights"
            ).forEach { item ->
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Surface(shape = CircleShape, color = BusinessBrand.copy(alpha = 0.12f), modifier = Modifier.size(28.dp)) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = BusinessBrand, modifier = Modifier.size(18.dp))
                        }
                    }
                    Text(item, style = MaterialTheme.typography.bodyLarge)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = onContinue,
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BusinessBrand)
            ) {
                Text("Continue")
            }
        }
    }
}

@Composable
fun BusinessAnalyticsScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Analytics") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) } }
            )
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
            EmptyState(
                title = "Analytics are not available yet",
                description = "Business metrics will appear here when analytics data is connected.",
                icon = Icons.Default.BarChart
            )
        }
    }
}

@Composable
fun BusinessCustomersScreen(
    onBack: () -> Unit,
    onCustomerClick: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Customers") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) } }
            )
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
            EmptyState(
                title = "Customer data is not available yet",
                description = "Customer profiles will appear here when the customer data source is connected.",
                icon = Icons.Default.People
            )
        }
    }
}

@Composable
fun BusinessCustomerDetailScreen(customerId: String?, onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Customer details") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) } }
            )
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
            EmptyState(
                title = "Customer details are not available",
                description = if (customerId.isNullOrBlank()) "No customer was selected." else "Customer details are not connected to a business data source yet.",
                icon = Icons.Default.People
            )
        }
    }
}

@Composable
fun BusinessNotificationsScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notifications") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) } }
            )
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
            EmptyState(
                title = "No notification history available",
                description = "Notifications received by this business account will appear here when history sync is available.",
                icon = Icons.Default.Notifications
            )
        }
    }
}

@Composable
fun BusinessReviewsScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Reviews & ratings") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) } }
            )
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
            EmptyState(
                title = "Reviews are not available yet",
                description = "Ratings and customer feedback will appear here when review data is connected.",
                icon = Icons.Default.Star
            )
        }
    }
}

@Composable
fun BusinessSettingsScreen(
    onBack: () -> Unit,
    onBusinessSettings: () -> Unit,
    onReservationSettings: () -> Unit,
    onSecuritySettings: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) } }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item { SettingItem("Business settings", "Store profile, contact, brand", onBusinessSettings) }
            item { SettingItem("Reservation settings", "Booking preferences and rules", onReservationSettings) }
            item { SettingItem("Security settings", "Password, access, sessions", onSecuritySettings) }
        }
    }
}

@Composable
fun BusinessBusinessSettingsScreen(onBack: () -> Unit) {
    SettingsUnavailableScreen(
        title = "Business settings",
        onBack = onBack,
        message = "Store and business preferences are not available in this view yet."
    )
}

@Composable
fun BusinessReservationSettingsScreen(onBack: () -> Unit) {
    SettingsUnavailableScreen(
        title = "Reservation settings",
        onBack = onBack,
        message = "Reservation preferences will appear here when the settings source is connected."
    )
}

@Composable
fun BusinessSecuritySettingsScreen(onBack: () -> Unit) {
    SettingsUnavailableScreen(
        title = "Security settings",
        onBack = onBack,
        message = "Security preferences are not available in this view yet."
    )
}

@Composable
fun BusinessSupportScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Help & support") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) } }
            )
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
            EmptyState(
                title = "Support contact is not configured",
                description = "Support options will appear here when contact details are provided.",
                icon = Icons.Default.HeadsetMic
            )
        }
    }
}

@Composable
fun BusinessSubscriptionScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Subscription") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) } }
            )
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
            EmptyState(
                title = "Plan information is not available",
                description = "Subscription and billing details will appear here when plan data is connected.",
                icon = Icons.Default.Star
            )
        }
    }
}

@Composable
private fun SettingsUnavailableScreen(title: String, onBack: () -> Unit, message: String) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) } }
            )
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
            EmptyState(
                title = "$title are not available",
                description = message,
                icon = Icons.Default.Settings
            )
        }
    }
}

@Composable
private fun SettingItem(title: String, subtitle: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = White)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(title, style = MaterialTheme.typography.titleMedium)
                Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
            Icon(Icons.Default.ChevronRight, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
