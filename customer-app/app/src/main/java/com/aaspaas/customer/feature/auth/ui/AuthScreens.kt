package com.aaspaas.customer.feature.auth.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.aaspaas.customer.core.ui.theme.*
import com.aaspaas.customer.feature.auth.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    onLoggedIn: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(state.isVerified) {
        if (state.isVerified) onLoggedIn()
    }

    if (state.otpSent) {
        OtpScreen(
            phone = viewModel.currentPhone,
            state = state,
            onVerify = { otp -> viewModel.verifyPhoneOtp(viewModel.currentPhone, otp, null, null, null) },
            onResend = { viewModel.requestPhoneOtp(viewModel.currentPhone) },
            onBack = { viewModel.resetOtp() }
        )
    } else {
        PhoneLoginScreen(
            state = state,
            onContinue = { phone -> viewModel.requestPhoneOtp(AuthViewModel.formatPhone(phone)) },
            onClearError = { viewModel.clearError() }
        )
    }
}

// ── Phone / Email Login Screen ────────────────────────────────────────────────

@Composable
private fun PhoneLoginScreen(
    state: com.aaspaas.customer.feature.auth.viewmodel.AuthUiState,
    onContinue: (String) -> Unit,
    onClearError: () -> Unit
) {
    var phone by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(24.dp)
            .imePadding(),
        verticalArrangement = Arrangement.spacedBy(0.dp)
    ) {
        Spacer(Modifier.height(48.dp))

        // Logo mark
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(Primary, RoundedCornerShape(16.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text("APW", style = MaterialTheme.typography.titleLarge, color = Color.White)
        }

        Spacer(Modifier.height(32.dp))

        Text(
            "Welcome to AasPaasWala",
            style = MaterialTheme.typography.headlineMedium,
            color = TextPrimary
        )
        Spacer(Modifier.height(8.dp))
        Text(
            "Discover products available near you.",
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary
        )

        Spacer(Modifier.height(40.dp))

        // Phone input
        Text("Phone number", style = MaterialTheme.typography.labelLarge, color = TextPrimary)
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = phone,
            onValueChange = {
                phone = it.filter { c -> c.isDigit() || c == '+' }.take(15)
                onClearError()
            },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("+91-XXXXXXXXXX", color = TextSecondary) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Primary,
                unfocusedBorderColor = Border
            ),
            enabled = !state.isLoading
        )

        state.error?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, color = Error, style = MaterialTheme.typography.bodySmall)
        }

        Spacer(Modifier.height(24.dp))

        Button(
            onClick = { onContinue(phone) },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            enabled = phone.filter(Char::isDigit).length >= 10 && !state.isLoading,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Primary)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
            } else {
                Text("Continue", style = MaterialTheme.typography.labelLarge)
            }
        }

        Spacer(Modifier.height(24.dp))

        // Divider
        Row(verticalAlignment = Alignment.CenterVertically) {
            HorizontalDivider(modifier = Modifier.weight(1f), color = Border)
            Text("  OR  ", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            HorizontalDivider(modifier = Modifier.weight(1f), color = Border)
        }

        Spacer(Modifier.height(24.dp))

        // Google sign-in (placeholder)
        OutlinedButton(
            onClick = {},
            modifier = Modifier.fillMaxWidth().height(52.dp),
            shape = RoundedCornerShape(12.dp),
            border = androidx.compose.foundation.BorderStroke(1.dp, Border)
        ) {
            Text("🇬  Continue with Google", style = MaterialTheme.typography.labelLarge, color = TextPrimary)
        }

        Spacer(Modifier.weight(1f))

        Text(
            "By continuing, you agree to our Terms & Privacy Policy.",
            style = MaterialTheme.typography.bodySmall,
            color = TextSecondary,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(Modifier.height(16.dp))
    }
}

// ── OTP Verification Screen ───────────────────────────────────────────────────

@Composable
private fun OtpScreen(
    phone: String,
    state: com.aaspaas.customer.feature.auth.viewmodel.AuthUiState,
    onVerify: (String) -> Unit,
    onResend: () -> Unit,
    onBack: () -> Unit
) {
    var otp by remember { mutableStateOf("") }
    var resendTimer by remember { mutableIntStateOf(24) }

    LaunchedEffect(Unit) {
        while (resendTimer > 0) {
            kotlinx.coroutines.delay(1000)
            resendTimer--
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(24.dp)
            .imePadding()
    ) {
        Spacer(Modifier.height(16.dp))
        IconButton(onClick = onBack, modifier = Modifier.offset(x = (-12).dp)) {
            Icon(Icons.Default.ArrowBack, null, tint = TextPrimary)
        }

        Spacer(Modifier.height(24.dp))

        Text("Verify your number", style = MaterialTheme.typography.headlineMedium, color = TextPrimary)
        Spacer(Modifier.height(8.dp))
        Text(
            "Enter the 6-digit code sent to $phone",
            style = MaterialTheme.typography.bodyLarge,
            color = TextSecondary
        )

        Spacer(Modifier.height(40.dp))

        // OTP boxes
        OtpInputRow(
            otp = otp,
            onOtpChange = { if (it.length <= 6) otp = it.filter(Char::isDigit) }
        )

        Spacer(Modifier.height(24.dp))

        // Resend timer
        if (resendTimer > 0) {
            Text(
                "Resend code in 00:${resendTimer.toString().padStart(2, '0')}",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            TextButton(
                onClick = { resendTimer = 24; onResend() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Resend Code", style = MaterialTheme.typography.labelLarge, color = Primary)
            }
        }

        state.error?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, color = Error, style = MaterialTheme.typography.bodySmall, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        }

        Spacer(Modifier.height(32.dp))

        Button(
            onClick = { onVerify(otp) },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            enabled = otp.length == 6 && !state.isLoading,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Primary)
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
            } else {
                Text("Verify", style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}

@Composable
private fun OtpInputRow(otp: String, onOtpChange: (String) -> Unit) {
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) { focusRequester.requestFocus() }

    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
        // Hidden real input
        OutlinedTextField(
            value = otp,
            onValueChange = onOtpChange,
            modifier = Modifier
                .size(1.dp)
                .focusRequester(focusRequester),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color.Transparent,
                unfocusedBorderColor = Color.Transparent
            )
        )

        // Visual OTP boxes
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            repeat(6) { index ->
                val char = otp.getOrNull(index)
                val isFocused = index == otp.length

                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .border(
                            width = if (isFocused) 2.dp else 1.dp,
                            color = when {
                                isFocused -> Primary
                                char != null -> TextPrimary
                                else -> Border
                            },
                            shape = RoundedCornerShape(10.dp)
                        )
                        .background(Surface, RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = char?.toString() ?: "",
                        style = MaterialTheme.typography.headlineSmall,
                        color = TextPrimary
                    )
                }
            }
        }
    }
}
