package com.aaspaas.customer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.aaspaas.customer.core.ui.theme.AasPaasWalaTheme
import com.aaspaas.customer.navigation.AppNavGraph
import com.aaspaas.customer.navigation.Screen
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var sessionManager: SessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AasPaasWalaTheme {
                val navController = rememberNavController()
                val isLoggedIn by sessionManager.isLoggedIn.collectAsStateWithLifecycle(initialValue = false)
                val onboardingDone by sessionManager.onboardingDone.collectAsStateWithLifecycle(initialValue = false)

                val startDestination = when {
                    isLoggedIn -> Screen.Home.route
                    else -> Screen.Splash.route
                }

                AppNavGraph(
                    navController = navController,
                    startDestination = startDestination
                )
            }
        }
    }
}
