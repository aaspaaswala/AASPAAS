package com.aaspaas.customer.feature.notifications.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.aaspaas.customer.core.ui.theme.*

data class NotificationItem(
    val id: String,
    val title: String,
    val body: String,
    val timeAgo: String,
    val isRead: Boolean,
    val type: NotificationType
)

enum class NotificationType { RESERVATION, OFFER, UPDATE, GENERAL }

@Composable
fun NotificationsScreen(onBack: () -> Unit) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("All", "Reservations", "Offers", "Updates")

    // Placeholder notifications
    val allNotifications = remember {
        listOf(
            NotificationItem("1", "Reservation confirmed", "Your Nike Shoes reservation has been confirmed at ABC Sports.", "10 min ago", false, NotificationType.RESERVATION),
            NotificationItem("2", "Reservation expiring soon", "Your reservation expires in 1 hour. Visit the store now.", "2 hrs ago", false, NotificationType.RESERVATION),
            NotificationItem("3", "Up to 30% OFF nearby", "Local stores near you are offering great deals today.", "1 day ago", true, NotificationType.OFFER),
            NotificationItem("4", "New store nearby", "ABC Electronics just opened 0.8 km from you.", "2 days ago", true, NotificationType.UPDATE),
            NotificationItem("5", "Reservation expired", "Your reservation for Running Shoes has expired.", "3 days ago", true, NotificationType.RESERVATION)
        )
    }

    val filtered = when (selectedTab) {
        1 -> allNotifications.filter { it.type == NotificationType.RESERVATION }
        2 -> allNotifications.filter { it.type == NotificationType.OFFER }
        3 -> allNotifications.filter { it.type == NotificationType.UPDATE }
        else -> allNotifications
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Notifications") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) }
                },
                actions = {
                    TextButton(onClick = {}) {
                        Text("Mark all read", style = MaterialTheme.typography.labelMedium, color = Primary)
                    }
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
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title, style = MaterialTheme.typography.labelLarge) }
                    )
                }
            }

            if (filtered.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text("🔔", style = MaterialTheme.typography.displayMedium)
                    Spacer(Modifier.height(16.dp))
                    Text("No notifications", style = MaterialTheme.typography.titleMedium, color = TextPrimary, textAlign = TextAlign.Center)
                    Spacer(Modifier.height(8.dp))
                    Text("You're all caught up!", style = MaterialTheme.typography.bodyMedium, color = TextSecondary, textAlign = TextAlign.Center)
                }
            } else {
                LazyColumn(contentPadding = PaddingValues(vertical = 8.dp)) {
                    items(filtered) { notification ->
                        NotificationCard(notification = notification)
                    }
                }
            }
        }
    }
}

@Composable
private fun NotificationCard(notification: NotificationItem) {
    val icon: ImageVector = when (notification.type) {
        NotificationType.RESERVATION -> Icons.Default.Bookmark
        NotificationType.OFFER -> Icons.Default.LocalOffer
        NotificationType.UPDATE -> Icons.Default.Store
        NotificationType.GENERAL -> Icons.Default.Notifications
    }
    val iconBg = when (notification.type) {
        NotificationType.RESERVATION -> Primary.copy(alpha = 0.1f)
        NotificationType.OFFER -> Accent.copy(alpha = 0.1f)
        NotificationType.UPDATE -> Success.copy(alpha = 0.1f)
        NotificationType.GENERAL -> TextSecondary.copy(alpha = 0.1f)
    }
    val iconTint = when (notification.type) {
        NotificationType.RESERVATION -> Primary
        NotificationType.OFFER -> Accent
        NotificationType.UPDATE -> Success
        NotificationType.GENERAL -> TextSecondary
    }

    Surface(
        color = if (!notification.isRead) Primary.copy(alpha = 0.03f) else Surface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.Top
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(iconBg),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, null, Modifier.size(22.dp), tint = iconTint)
            }

            Spacer(Modifier.width(12.dp))

            Column(Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        notification.title,
                        style = MaterialTheme.typography.titleSmall,
                        color = TextPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    if (!notification.isRead) {
                        Spacer(Modifier.width(8.dp))
                        Box(
                            Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(Primary)
                        )
                    }
                }
                Spacer(Modifier.height(4.dp))
                Text(
                    notification.body,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    notification.timeAgo,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextSecondary.copy(alpha = 0.7f)
                )
            }
        }
        HorizontalDivider(color = Border, modifier = Modifier.padding(start = 72.dp))
    }
}
