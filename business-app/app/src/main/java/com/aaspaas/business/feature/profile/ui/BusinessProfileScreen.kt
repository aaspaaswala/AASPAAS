package com.aaspaas.business.feature.profile.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aaspaas.business.core.ui.theme.BusinessAccent
import com.aaspaas.business.core.ui.theme.BusinessBrand
import com.aaspaas.business.core.ui.components.BusinessAvatar
import com.aaspaas.business.feature.profile.viewmodel.BusinessProfileViewModel

@Composable
fun BusinessProfileScreen(
    onLogout: () -> Unit,
    onBack: () -> Unit,
    onSettingsClick: () -> Unit = {},
    onSupportClick: () -> Unit = {},
    onSubscriptionClick: () -> Unit = {},
    onStoreClick: () -> Unit = {},
    viewModel: BusinessProfileViewModel = hiltViewModel()
) {
    val retailer by viewModel.retailer.collectAsStateWithLifecycle()
    var showLogoutDialog by remember { mutableStateOf(false) }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Logout") },
            text = { Text("Are you sure you want to logout?") },
            confirmButton = { TextButton(onClick = { viewModel.logout(onLogout) }) { Text("Logout", color = MaterialTheme.colorScheme.error) } },
            dismissButton = { TextButton(onClick = { showLogoutDialog = false }) { Text("Cancel") } }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) } }
            )
        }
    ) { padding ->
        Column(Modifier.padding(padding).padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                BusinessAvatar(retailer?.ownerName, modifier = Modifier.size(64.dp))
                Spacer(Modifier.width(16.dp))
                Column {
                    Text(retailer?.businessName ?: "Business", style = MaterialTheme.typography.headlineSmall)
                    Text(retailer?.ownerName ?: "", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(retailer?.mobile ?: "", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            Spacer(Modifier.height(24.dp))
            HorizontalDivider()
            Spacer(Modifier.height(8.dp))

            ProfileMenuItem("Store profile", Icons.Default.Store, onStoreClick)
            ProfileMenuItem("Settings", Icons.Default.Settings, onSettingsClick)
            ProfileMenuItem("Help & support", Icons.Default.HeadsetMic, onSupportClick)
            ProfileMenuItem("Subscription", Icons.Default.Star, onSubscriptionClick)
            Spacer(Modifier.height(12.dp))

            Surface(modifier = Modifier.fillMaxWidth(), onClick = { showLogoutDialog = true }, shape = RoundedCornerShape(8.dp)) {
                Row(Modifier.padding(horizontal = 8.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Logout, null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(22.dp))
                    Spacer(Modifier.width(16.dp))
                    Text("Logout", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.error)
                }
            }
        }
    }
}

@Composable
private fun ProfileMenuItem(label: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Surface(modifier = Modifier.fillMaxWidth(), onClick = onClick, shape = RoundedCornerShape(10.dp)) {
        Row(Modifier.padding(horizontal = 12.dp, vertical = 14.dp), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, null, tint = BusinessBrand, modifier = Modifier.size(20.dp))
            Spacer(Modifier.width(12.dp))
            Text(label, style = MaterialTheme.typography.bodyLarge)
        }
    }
    Spacer(Modifier.height(8.dp))
}
