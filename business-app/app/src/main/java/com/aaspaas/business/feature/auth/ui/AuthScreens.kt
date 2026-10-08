package com.aaspaas.business.feature.auth.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import android.app.Activity
import android.util.Patterns
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.aaspaas.business.BuildConfig
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
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
import com.aaspaas.business.core.ui.components.BrandButton
import com.aaspaas.business.core.ui.components.BrandOutlinedButton
import com.aaspaas.business.feature.auth.viewmodel.BusinessAuthViewModel

private enum class BusinessAuthMethod { EMAIL, PHONE }

@Composable
fun BusinessSplashScreen(
    onNavigateToAuth: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(BusinessBrand, BusinessAccent)))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 28.dp, vertical = 36.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(shape = RoundedCornerShape(16.dp), color = White.copy(alpha = 0.12f)) {
                    Icon(
                        Icons.Default.Storefront,
                        contentDescription = null,
                        tint = White,
                        modifier = Modifier.padding(10.dp).size(24.dp)
                    )
                }
                Spacer(Modifier.width(10.dp))
                Text("AASPAASWALA", style = MaterialTheme.typography.titleMedium, color = White)
            }

            Column(verticalArrangement = Arrangement.spacedBy(22.dp)) {
                Surface(shape = RoundedCornerShape(24.dp), color = White.copy(alpha = 0.12f)) {
                    Row(
                        modifier = Modifier.padding(horizontal = 18.dp, vertical = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Storefront, contentDescription = null, tint = Color(0xFFFFC857), modifier = Modifier.size(34.dp))
                        Column {
                            Text("YOUR STORE", style = MaterialTheme.typography.labelSmall, color = White.copy(alpha = 0.72f))
                            Text("Ready to grow", style = MaterialTheme.typography.titleMedium, color = White)
                        }
                    }
                }
                Text("Grow Your Business", style = MaterialTheme.typography.displaySmall, color = White)
                Text(
                    "Be Closer to Your Customers",
                    style = MaterialTheme.typography.bodyLarge,
                    color = White.copy(alpha = 0.82f)
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    onClick = onNavigateToRegister,
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = White)
                ) {
                    Text("Get Started", style = MaterialTheme.typography.labelLarge, color = BusinessBrand)
                }
                TextButton(onClick = onNavigateToAuth, modifier = Modifier.fillMaxWidth()) {
                    Text("Already have an account? Log in", color = White)
                }
            }
        }
    }
}

@Composable
fun BusinessLoginScreen(
    onLoggedIn: () -> Unit,
    onRegisterClick: () -> Unit,
    viewModel: BusinessAuthViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var authMethod by remember { mutableStateOf(BusinessAuthMethod.EMAIL) }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }
    var googleError by remember { mutableStateOf<String?>(null) }
    var validationError by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current
    val googleWebClientId = BuildConfig.GOOGLE_WEB_CLIENT_ID
    val googleSignInClient = remember(context, googleWebClientId) {
        GoogleSignIn.getClient(
            context,
            GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(googleWebClientId)
                .build()
        )
    }
    val googleSignInLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            try {
                val account = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                    .getResult(ApiException::class.java)
                val idToken = account.idToken
                if (idToken.isNullOrBlank()) googleError = "Google did not return an ID token. Check your OAuth setup."
                else viewModel.socialLogin("google", idToken)
            } catch (error: ApiException) {
                googleError = error.localizedMessage ?: "Google sign-in could not be completed."
            }
        }
    }

    LaunchedEffect(state.isVerified) { if (state.isVerified) onLoggedIn() }

    val identifierLabel = if (authMethod == BusinessAuthMethod.EMAIL) "Email" else "Mobile Number"
    val identifierPlaceholder = if (authMethod == BusinessAuthMethod.EMAIL) "email@example.com" else "+91 XXXXXXXXXX"
    val isPhoneNumberValid = phone.replace(" ", "").length >= 10
    val isEmailValid = Patterns.EMAIL_ADDRESS.matcher(email.trim()).matches()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .imePadding()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier.size(80.dp).background(BusinessBrand, RoundedCornerShape(20.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text("APW", style = MaterialTheme.typography.headlineMedium, color = White)
        }
        Spacer(Modifier.height(24.dp))
        Text("Welcome Back!", style = MaterialTheme.typography.headlineMedium, color = BusinessBrand)
        Text("Login to your business account", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val selectedModifier = Modifier.weight(1f).height(46.dp)
            val unselectedModifier = Modifier.weight(1f).height(46.dp)

            FilterChip(
                selected = authMethod == BusinessAuthMethod.EMAIL,
                onClick = { authMethod = BusinessAuthMethod.EMAIL; validationError = null; otp = "" },
                label = { Text("Email") },
                modifier = selectedModifier,
                shape = RoundedCornerShape(12.dp)
            )
            FilterChip(
                selected = authMethod == BusinessAuthMethod.PHONE,
                onClick = { authMethod = BusinessAuthMethod.PHONE; validationError = null; otp = "" },
                label = { Text("Mobile Number") },
                modifier = unselectedModifier,
                shape = RoundedCornerShape(12.dp)
            )
        }

        Spacer(Modifier.height(20.dp))

        if (authMethod == BusinessAuthMethod.EMAIL) {
            OutlinedTextField(
                value = email,
                onValueChange = { email = it.trim() },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Email") },
                placeholder = { Text("email@example.com") },
                enabled = !state.otpSent,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                shape = RoundedCornerShape(16.dp)
            )
        } else {
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it.filter { c -> c.isDigit() || c == '+' }.take(15) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Mobile Number") },
                placeholder = { Text("+91 XXXXXXXXXX") },
                enabled = !state.otpSent,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true,
                shape = RoundedCornerShape(16.dp)
            )
        }

        if (state.otpSent) {
            Spacer(Modifier.height(12.dp))
            Text(
                text = if (authMethod == BusinessAuthMethod.EMAIL) "Enter the 6 digit code sent to your email" else "Enter the 6 digit code sent to ${phone.ifBlank { "+91 XXXXX XXXXX" }}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = otp,
                onValueChange = { if (it.length <= 6) otp = it.filter(Char::isDigit) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Verify OTP") },
                placeholder = { Text("6-digit code") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                shape = RoundedCornerShape(16.dp)
            )
        }

        (validationError ?: googleError ?: state.error)?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

        Spacer(Modifier.height(24.dp))
        val canRequestOtp = if (authMethod == BusinessAuthMethod.EMAIL) {
            email.isNotBlank() && isEmailValid && !state.isLoading
        } else {
            phone.isNotBlank() && isPhoneNumberValid && !state.isLoading
        }
        val canVerifyOtp = otp.length == 6 && !state.isLoading

        BrandButton(
            text = if (state.otpSent) "Verify OTP" else "Send OTP",
            onClick = {
                validationError = null
                if (state.otpSent) {
                    if (authMethod == BusinessAuthMethod.EMAIL) {
                        if (email.isBlank() || !isEmailValid) {
                            validationError = "Please enter a valid email address."
                            return@BrandButton
                        }
                        viewModel.verifyEmailOtp(email, otp)
                    } else {
                        if (!isPhoneNumberValid) {
                            validationError = "Please enter a valid mobile number."
                            return@BrandButton
                        }
                        viewModel.verifyPhoneOtp(phone, otp)
                    }
                } else {
                    if (authMethod == BusinessAuthMethod.EMAIL) {
                        if (!isEmailValid) {
                            validationError = "Please enter a valid email address."
                            return@BrandButton
                        }
                        viewModel.requestEmailOtp(email)
                    } else {
                        if (!isPhoneNumberValid) {
                            validationError = "Please enter a valid mobile number."
                            return@BrandButton
                        }
                        viewModel.requestPhoneOtp(phone)
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = if (state.otpSent) canVerifyOtp else canRequestOtp,
            isLoading = state.isLoading
        )

        if (state.otpSent) {
            Spacer(Modifier.height(12.dp))
            TextButton(
                onClick = {
                    validationError = null
                    if (authMethod == BusinessAuthMethod.EMAIL) {
                        if (!isEmailValid) {
                            validationError = "Please enter a valid email address."
                            return@TextButton
                        }
                        viewModel.requestEmailOtp(email)
                    } else {
                        if (!isPhoneNumberValid) {
                            validationError = "Please enter a valid mobile number."
                            return@TextButton
                        }
                        viewModel.requestPhoneOtp(phone)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading
            ) {
                Text("Resend OTP")
            }
        }

        Spacer(Modifier.height(14.dp))
        OutlinedButton(
            onClick = {
                googleError = null
                if (googleWebClientId.isBlank()) {
                    googleError = "Add GOOGLE_WEB_CLIENT_ID to local.properties to enable Google sign-in."
                } else {
                    googleSignInLauncher.launch(googleSignInClient.signInIntent)
                }
            },
            modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp),
            enabled = !state.isLoading,
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("G", style = MaterialTheme.typography.titleLarge, color = Color(0xFF4285F4))
            Spacer(Modifier.width(10.dp))
            Text("Continue with Google", style = MaterialTheme.typography.labelLarge)
        }
        Spacer(Modifier.height(16.dp))
        TextButton(
            onClick = onRegisterClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Don't have an account? Sign Up")
        }
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
    var openingHours by remember { mutableStateOf("") }

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
                Triple("Opening Hours *", openingHours, { v: String -> openingHours = v })
            ).forEach { (label, value, onChange) ->
                OutlinedTextField(
                    value = value,
                    onValueChange = onChange,
                    label = { Text(label) },
                    placeholder = { if (label == "Opening Hours *") Text("e.g. 9:00 AM - 9:00 PM") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = label != "Address *"
                )
            }

            state.error?.let {
                Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
            }

            Spacer(Modifier.height(8.dp))
            BrandButton(
                text = "Register Business",
                onClick = {
                    viewModel.register(
                        ownerName = ownerName, businessName = businessName,
                        mobile = mobile, email = email.ifBlank { null },
                        category = category, address = address,
                        latitude = 0.0, longitude = 0.0,
                        openingHours = openingHours
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = ownerName.isNotBlank() && businessName.isNotBlank() &&
                    mobile.length == 10 && openingHours.isNotBlank(),
                isLoading = state.isLoading
            )
        }
    }
}
