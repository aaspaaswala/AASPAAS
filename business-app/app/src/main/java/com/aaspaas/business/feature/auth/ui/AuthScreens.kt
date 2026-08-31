package com.aaspaas.business.feature.auth.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aaspaas.business.core.ui.theme.BusinessAccent
import com.aaspaas.business.core.ui.theme.BusinessBrand
import com.aaspaas.business.core.ui.theme.White
import com.aaspaas.business.feature.auth.viewmodel.BusinessAuthViewModel
import kotlinx.coroutines.delay

@Composable
fun BusinessSplashScreen(
    onNavigateToAuth: () -> Unit,
    onNavigateToDashboard: () -> Unit,
    viewModel: BusinessAuthViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(1000)
        if (viewModel.isLoggedIn()) onNavigateToDashboard() else onNavigateToAuth()
    }
    Box(Modifier.fillMaxSize().background(BusinessBrand), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("AAS PAAS WALA", style = MaterialTheme.typography.displayMedium, color = White)
            Spacer(Modifier.height(4.dp))
            Text("BUSINESS", style = MaterialTheme.typography.headlineMedium, color = BusinessAccent)
        }
    }
}

@Composable
fun BusinessPhoneAuthScreen(
    onOtpSent: (String) -> Unit,
    onRegisterClick: () -> Unit,
    viewModel: BusinessAuthViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var mobile by remember { mutableStateOf("") }

    LaunchedEffect(state.otpSent) { if (state.otpSent) onOtpSent(mobile) }

    Column(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Welcome Back", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text("APW Business", style = MaterialTheme.typography.displayMedium, color = BusinessBrand)
        Spacer(Modifier.height(48.dp))

        Text("Enter your mobile number", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(12.dp))
        OutlinedTextField(
            value = mobile,
            onValueChange = { if (it.length <= 10) mobile = it.filter { c -> c.isDigit() } },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("10-digit mobile number") },
            prefix = { Text("+91  ") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )

        state.error?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

        Spacer(Modifier.height(24.dp))
        Button(
            onClick = { viewModel.sendOtp(mobile) },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            enabled = mobile.length == 10 && !state.isLoading,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BusinessAccent)
        ) {
            if (state.isLoading) CircularProgressIndicator(Modifier.size(20.dp), color = White, strokeWidth = 2.dp)
            else Text("Send OTP", style = MaterialTheme.typography.labelLarge)
        }

        Spacer(Modifier.height(16.dp))
        OutlinedButton(
            onClick = onRegisterClick,
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(12.dp)
        ) { Text("Register New Business") }
    }
}

@Composable
fun BusinessOtpScreen(
    mobile: String,
    onVerified: () -> Unit,
    onBack: () -> Unit,
    viewModel: BusinessAuthViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var otp by remember { mutableStateOf("") }

    LaunchedEffect(state.isVerified) { if (state.isVerified) onVerified() }

    Column(
        modifier = Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background).padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text("Verify OTP", style = MaterialTheme.typography.headlineLarge, color = BusinessBrand)
        Spacer(Modifier.height(8.dp))
        Text("Sent to +91 $mobile", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(40.dp))

        OutlinedTextField(
            value = otp,
            onValueChange = { if (it.length <= 6) otp = it.filter { c -> c.isDigit() } },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("6-digit OTP") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )

        state.error?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

        Spacer(Modifier.height(24.dp))
        Button(
            onClick = { viewModel.verifyOtp(mobile, otp) },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            enabled = otp.length == 6 && !state.isLoading,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BusinessAccent)
        ) {
            if (state.isLoading) CircularProgressIndicator(Modifier.size(20.dp), color = White, strokeWidth = 2.dp)
            else Text("Verify & Continue", style = MaterialTheme.typography.labelLarge)
        }

        Spacer(Modifier.height(16.dp))
        TextButton(onClick = onBack, modifier = Modifier.align(Alignment.CenterHorizontally)) { Text("Change number") }
    }
}

@Composable
fun BusinessRegisterScreen(
    onRegistered: () -> Unit,
    onBack: () -> Unit,
    viewModel: BusinessAuthViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var ownerName by remember { mutableStateOf("") }
    var businessName by remember { mutableStateOf("") }
    var mobile by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var openingHours by remember { mutableStateOf("9:00 AM - 9:00 PM") }

    LaunchedEffect(state.isRegistered) { if (state.isRegistered) onRegistered() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Register Business") },
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, null) } }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Business Details", style = MaterialTheme.typography.headlineSmall, color = BusinessBrand)

            listOf(
                Triple("Owner Name *", ownerName, { v: String -> ownerName = v }),
                Triple("Business Name *", businessName, { v: String -> businessName = v }),
                Triple("Mobile *", mobile, { v: String -> if (v.length <= 10) mobile = v.filter { c -> c.isDigit() } }),
                Triple("Email (optional)", email, { v: String -> email = v }),
                Triple("Category (e.g. Clothing, Electronics)", category, { v: String -> category = v }),
                Triple("Address *", address, { v: String -> address = v }),
                Triple("Opening Hours", openingHours, { v: String -> openingHours = v })
            ).forEach { (label, value, onChange) ->
                OutlinedTextField(
                    value = value,
                    onValueChange = onChange,
                    label = { Text(label) },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    singleLine = label != "Address *"
                )
            }

            state.error?.let {
                Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(Modifier.height(8.dp))
            Button(
                onClick = {
                    viewModel.register(
                        ownerName = ownerName, businessName = businessName,
                        mobile = mobile, email = email.ifBlank { null },
                        category = category, address = address,
                        latitude = 0.0, longitude = 0.0, // Location picker in Phase 2
                        openingHours = openingHours
                    )
                },
                modifier = Modifier.fillMaxWidth().height(52.dp),
                enabled = ownerName.isNotBlank() && businessName.isNotBlank() && mobile.length == 10 && !state.isLoading,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BusinessAccent)
            ) {
                if (state.isLoading) CircularProgressIndicator(Modifier.size(20.dp), color = White, strokeWidth = 2.dp)
                else Text("Register Business", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}
