package com.aaspaas.customer.feature.store.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aaspaas.customer.core.ui.components.ErrorScreen
import com.aaspaas.customer.core.ui.components.LoadingScreen
import com.aaspaas.customer.core.ui.theme.*
import com.aaspaas.customer.feature.store.viewmodel.StoreMapViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun StoreMapScreen(
    storeId: String,
    onBack: () -> Unit,
    viewModel: StoreMapViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(storeId) {
        viewModel.load(storeId)
    }

    when {
        state.isLoading -> LoadingScreen()
        state.error != null -> ErrorScreen(state.error!!) { viewModel.load(storeId) }
        state.store != null -> {
            val store = state.store!!
            val storeLatLng = LatLng(store.latitude, store.longitude)

            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text(store.name) },
                        navigationIcon = {
                            IconButton(onClick = onBack) {
                                Icon(Icons.Default.ArrowBack, null, tint = TextPrimary)
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = Surface)
                    )
                },
                containerColor = Background,
                floatingActionButton = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        store.phone?.let { phone ->
                            FloatingActionButton(
                                onClick = {
                                    context.startActivity(Intent(Intent.ACTION_DIAL, Uri.parse("tel:$phone")))
                                },
                                containerColor = Primary,
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Phone, null, tint = Color.White)
                            }
                        }
                        FloatingActionButton(
                            onClick = {
                                val uri = Uri.parse("google.navigation:q=${store.latitude},${store.longitude}")
                                context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                            },
                            containerColor = Primary,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Directions, null, tint = Color.White)
                        }
                    }
                }
            ) { padding ->
                Box(Modifier.padding(padding)) {
                    val cameraPositionState = rememberCameraPositionState {
                        position = CameraPosition.fromLatLngZoom(storeLatLng, 16f)
                    }

                    GoogleMap(
                        modifier = Modifier.matchParentSize(),
                        cameraPositionState = cameraPositionState,
                        properties = MapProperties(mapType = MapType.NORMAL)
                    ) {
                        Marker(
                            state = com.google.maps.android.compose.MarkerState(position = storeLatLng),
                            title = store.name,
                            snippet = store.address
                        )
                    }

                    Surface(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(16.dp)
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        tonalElevation = 4.dp
                    ) {
                        Column(Modifier.padding(16.dp)) {
                            Text(store.name, style = MaterialTheme.typography.titleMedium, color = TextPrimary)
                            Text(store.address, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                            Spacer(Modifier.height(8.dp))
                            store.phone?.let { phone ->
                                Text(phone, style = MaterialTheme.typography.bodyMedium, color = TextPrimary)
                            }
                        }
                    }
                }
            }
        }
    }
}
