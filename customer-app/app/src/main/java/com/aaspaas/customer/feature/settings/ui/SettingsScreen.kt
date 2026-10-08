package com.aaspaas.customer.feature.settings.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.aaspaas.customer.core.ui.theme.*

@Composable
fun SettingsScreen(onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Surface)
            )
        },
        containerColor = Background
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // Account
            item {
                SettingsSectionHeader("Account")
                Surface(color = Surface) {
                    Column {
                        SettingsItem(
                            icon = Icons.Outlined.Person,
                            label = "Personal Information",
                            onClick = {}
                        )
                        SettingsDivider()
                        SettingsItem(
                            icon = Icons.Outlined.LocationOn,
                            label = "Saved Locations",
                            onClick = {}
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
            }

            // Preferences
            item {
                SettingsSectionHeader("Preferences")
                Surface(color = Surface) {
                    Column {
                        SettingsToggleItem(
                            icon = Icons.Outlined.Notifications,
                            label = "Push Notifications",
                            checked = true,
                            onToggle = {}
                        )
                        SettingsDivider()
                        SettingsItem(
                            icon = Icons.Outlined.Language,
                            label = "Language",
                            value = "English",
                            onClick = {}
                        )
                        SettingsDivider()
                        SettingsItem(
                            icon = Icons.Outlined.LocationOn,
                            label = "Location",
                            value = "Enabled",
                            onClick = {}
                        )
                        SettingsDivider()
                        SettingsToggleItem(
                            icon = Icons.Outlined.DarkMode,
                            label = "Dark Mode",
                            checked = false,
                            onToggle = {}
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
            }

            // Security
            item {
                SettingsSectionHeader("Security")
                Surface(color = Surface) {
                    Column {
                        SettingsItem(
                            icon = Icons.Outlined.Lock,
                            label = "Login & Security",
                            onClick = {}
                        )
                    }
                }
                Spacer(Modifier.height(8.dp))
            }

            // About
            item {
                SettingsSectionHeader("About")
                Surface(color = Surface) {
                    Column {
                        SettingsItem(
                            icon = Icons.Outlined.Shield,
                            label = "Privacy Policy",
                            onClick = {}
                        )
                        SettingsDivider()
                        SettingsItem(
                            icon = Icons.Outlined.Description,
                            label = "Terms of Service",
                            onClick = {}
                        )
                        SettingsDivider()
                        SettingsItem(
                            icon = Icons.Outlined.Info,
                            label = "About AasPaasWala",
                            value = "v1.0.0",
                            onClick = {}
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingsSectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelMedium,
        color = TextSecondary,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
private fun SettingsDivider() {
    HorizontalDivider(color = Border, modifier = Modifier.padding(start = 56.dp))
}

@Composable
private fun SettingsItem(
    icon: ImageVector,
    label: String,
    value: String? = null,
    onClick: () -> Unit
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
            Icon(icon, null, Modifier.size(22.dp), tint = TextPrimary)
            Spacer(Modifier.width(16.dp))
            Text(label, style = MaterialTheme.typography.bodyLarge, color = TextPrimary, modifier = Modifier.weight(1f))
            value?.let {
                Text(it, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                Spacer(Modifier.width(4.dp))
            }
            Icon(Icons.Default.ChevronRight, null, Modifier.size(20.dp), tint = TextSecondary)
        }
    }
}

@Composable
private fun SettingsToggleItem(
    icon: ImageVector,
    label: String,
    checked: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, Modifier.size(22.dp), tint = TextPrimary)
        Spacer(Modifier.width(16.dp))
        Text(label, style = MaterialTheme.typography.bodyLarge, color = TextPrimary, modifier = Modifier.weight(1f))
        Switch(
            checked = checked,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Surface,
                checkedTrackColor = Primary
            )
        )
    }
}
