package com.aaspaas.business

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.rememberNavController
import com.aaspaas.business.core.ui.theme.AasPaasBusinessTheme
import com.aaspaas.business.navigation.BusinessNavGraph
import com.aaspaas.business.navigation.BusinessScreen
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import androidx.compose.runtime.getValue

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject lateinit var sessionManager: BusinessSessionManager

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AasPaasBusinessTheme {
                val navController = rememberNavController()
                val isLoggedIn by sessionManager.isLoggedIn.collectAsStateWithLifecycle(initialValue = false)
                BusinessNavGraph(
                    navController = navController,
                    startDestination = if (isLoggedIn) BusinessScreen.Dashboard.route else BusinessScreen.Splash.route
                )
            }
        }
    }
}
