package com.aaspaas.customer.feature.profile.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aaspaas.customer.core.ui.theme.*
import com.aaspaas.customer.feature.profile.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(
    onReservationsClick: () -> Unit,
    onWishlistClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onSettingsClick: () -> Unit = {},
    onLogout: () -> Unit,
    onBack: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val user by viewModel.user.collectAsStateWithLifecycle()
    var showLogoutDialog by remember { mutableStateOf(false) }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Logout") },
            text = { Text("Are you sure you want to logout?") },
            confirmButton = {
                TextButton(onClick = { viewModel.logout(onLogout) }) {
                    Text("Logout", color = Error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) { Text("Cancel") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile") },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Surface)
            )
        },
        containerColor = Background
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // User header card
            item {
                Surface(color = Surface) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Avatar
                        Surface(
                            shape = CircleShape,
                            color = Primary,
                            modifier = Modifier.size(64.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = user?.name?.firstOrNull()?.uppercase() ?: "?",
                                    style = MaterialTheme.typography.headlineMedium,
                                    color = Color.White
                                )
                            }
                        }
                        Spacer(Modifier.width(16.dp))
                        Column(Modifier.weight(1f)) {
                            Text(user?.name ?: "User", style = MaterialTheme.typography.titleLarge, color = TextPrimary)
                            user?.email?.let {
                                Text(it, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                            }
                            user?.mobile?.let {
                                Text(it, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                            }
                        }
                        TextButton(onClick = {}) {
                            Text("Edit", style = MaterialTheme.typography.labelMedium, color = Primary)
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
            }

            // My Activity section
            item {
                MenuSectionHeader("My Activity")
                Surface(color = Surface) {
                    Column {
                        ProfileMenuItem(
                            icon = Icons.Outlined.BookmarkBorder,
                            label = "My Reservations",
                            onClick = onReservationsClick
                        )
                        MenuDivider()
                        ProfileMenuItem(
                            icon = Icons.Outlined.FavoriteBorder,
                            label = "Wishlist",
                            onClick = onWishlistClick
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
            }

            // Account section
            item {
                MenuSectionHeader("Account")
                Surface(color = Surface) {
                    Column {
                        ProfileMenuItem(
                            icon = Icons.Outlined.LocationOn,
                            label = "Saved Locations",
                            onClick = {}
                        )
                        MenuDivider()
                        ProfileMenuItem(
                            icon = Icons.Outlined.Notifications,
                            label = "Notifications",
                            onClick = onNotificationsClick
                        )
                        MenuDivider()
                        ProfileMenuItem(
                            icon = Icons.Outlined.Settings,
                            label = "Settings",
                            onClick = onSettingsClick
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
            }

            // Subscription section
            item {
                MenuSectionHeader("Subscription")
                Surface(color = Surface) {
                    Column {
                        ProfileMenuItem(
                            icon = Icons.Outlined.Star,
                            label = "AasPaasWala Plus",
                            onClick = {},
                            badge = "PRO",
                            badgeColor = Accent
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
            }

            // Support section
            item {
                MenuSectionHeader("Support")
                Surface(color = Surface) {
                    Column {
                        ProfileMenuItem(
                            icon = Icons.Outlined.HelpOutline,
                            label = "Help & Support",
                            onClick = {}
                        )
                        MenuDivider()
                        ProfileMenuItem(
                            icon = Icons.Outlined.Shield,
                            label = "Terms & Privacy",
                            onClick = {}
                        )
                        MenuDivider()
                        ProfileMenuItem(
                            icon = Icons.Outlined.Info,
                            label = "About AasPaasWala",
                            onClick = {}
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
            }

            // Logout
            item {
                Surface(color = Surface) {
                    ProfileMenuItem(
                        icon = Icons.Default.Logout,
                        label = "Logout",
                        onClick = { showLogoutDialog = true },
                        tint = Error,
                        showChevron = false
                    )
                }
            }
        }
    }
}

@Composable
private fun MenuSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelMedium,
        color = TextSecondary,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
private fun MenuDivider() {
    HorizontalDivider(
        color = Border,
        modifier = Modifier.padding(start = 56.dp)
    )
}

@Composable
private fun ProfileMenuItem(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit,
    badge: String? = null,
    badgeColor: Color = Primary,
    tint: Color = TextPrimary,
    showChevron: Boolean = true
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(22.dp))
            Spacer(Modifier.width(16.dp))
            Text(
                label,
                style = MaterialTheme.typography.bodyLarge,
                color = tint,
                modifier = Modifier.weight(1f)
            )
            badge?.let {
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = badgeColor.copy(alpha = 0.12f)
                ) {
                    Text(
                        it,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = badgeColor
                    )
                }
                Spacer(Modifier.width(8.dp))
            }
            if (showChevron) {
                Icon(Icons.Default.ChevronRight, null, tint = TextSecondary, modifier = Modifier.size(20.dp))
            }
        }
    }
}
