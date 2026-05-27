package com.example.echoro.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.echoro.network.TokenStore
import com.example.echoro.ui.screens.admin.AdminDashboardScreen
import com.example.echoro.ui.screens.feedback.FeedbackScreen
import com.example.echoro.ui.screens.generatevoice.GenerateVoiceScreen
import com.example.echoro.ui.screens.landingpage.LandingScreen
import com.example.echoro.ui.screens.login.LoginScreen
import com.example.echoro.ui.screens.login.RegisterScreen
import com.example.echoro.ui.screens.splash.SplashScreen
import com.example.echoro.viewmodel.auth.SessionManager
import kotlinx.coroutines.launch

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    val context = LocalContext.current
    val sessionManager = remember { SessionManager(context) }
    val coroutineScope = rememberCoroutineScope()

    val currentUserId by sessionManager.userIdFlow.collectAsState(initial = 0)
    val currentToken by sessionManager.tokenFlow.collectAsState(initial = null)

    LaunchedEffect(currentToken) {
        currentToken?.let { TokenStore.token = it }
    }

    NavHost(
        navController = navController,
        startDestination = Screen.Splash.route
    ) {

        composable(Screen.Splash.route) {
            SplashScreen(
                onNavigateNext = {
                    navController.navigate(Screen.Landing.route) {
                        popUpTo(Screen.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Landing.route) {
            LandingScreen(
                onLoginClick = { navController.navigate(Screen.Login.route) },
                onRegisterClick = { navController.navigate(Screen.Register.route) },
                onGuestClick = {
                    coroutineScope.launch { sessionManager.saveSession(0, true) }
                    navController.navigate(Screen.GenerateVoice.createRoute(isGuest = true))
                }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                onNavigateToApp = { userId, role ->
                    coroutineScope.launch {
                        sessionManager.saveSession(userId, false)
                        sessionManager.saveToken(TokenStore.token)
                    }
                    if (role == "admin") {
                        navController.navigate(Screen.AdminDashboard.route) { popUpTo(Screen.Landing.route) { inclusive = true } }
                    } else {
                        navController.navigate(Screen.GenerateVoice.createRoute(isGuest = false)) { popUpTo(Screen.Landing.route) { inclusive = true } }
                    }
                },
                onCreateAccountClick = { navController.navigate(Screen.Register.route) }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onNavigateToApp = { userId, role ->
                    coroutineScope.launch {
                        sessionManager.saveSession(userId, false)
                        sessionManager.saveToken(TokenStore.token)
                    }
                    if (role == "admin") {
                        navController.navigate(Screen.AdminDashboard.route) { popUpTo(Screen.Landing.route) { inclusive = true } }
                    } else {
                        navController.navigate(Screen.GenerateVoice.createRoute(isGuest = false)) { popUpTo(Screen.Landing.route) { inclusive = true } }
                    }
                },
                onLoginClick = { navController.popBackStack() },
                onContinueAsGuest = {
                    coroutineScope.launch { sessionManager.saveSession(0, true) }
                    navController.navigate(Screen.GenerateVoice.createRoute(isGuest = true))
                }
            )
        }

        composable(
            route = Screen.GenerateVoice.route,
            arguments = listOf(navArgument("isGuest") { type = NavType.BoolType })
        ) { backStackEntry ->
            val isGuest = backStackEntry.arguments?.getBoolean("isGuest") ?: true

            GenerateVoiceScreen(
                isGuest = isGuest,
                userId = currentUserId,
                onNavigateToFeedback = { url, text, model ->
                    val encodedUrl = java.net.URLEncoder.encode(url, "UTF-8")
                    val encodedText = java.net.URLEncoder.encode(text, "UTF-8")

                    navController.navigate("feedback_route?audioUrl=$encodedUrl&textUsed=$encodedText&modelType=$model")
                },
                onLoginClick = { navController.navigate(Screen.Login.route) },
                onLogoutClick = {
                    coroutineScope.launch { sessionManager.clearSession() }
                    navController.navigate(Screen.Landing.route) {
                        popUpTo(0)
                    }
                }
            )
        }

        composable(
            route = "feedback_route?audioUrl={audioUrl}&textUsed={textUsed}&modelType={modelType}",            arguments = listOf(
                navArgument("audioUrl") {
                    type = NavType.StringType
                    defaultValue = ""
                },
                navArgument("textUsed") {
                    type = NavType.StringType
                    defaultValue = ""
                },
                navArgument("modelType") {
                    type = NavType.StringType
                    defaultValue = "Mini"
                }
            )
        ) { backStackEntry ->
            val encodedUrl = backStackEntry.arguments?.getString("audioUrl") ?: ""
            val encodedText = backStackEntry.arguments?.getString("textUsed") ?: ""
            val modelType = backStackEntry.arguments?.getString("modelType") ?: "Mini"

            val audioUrl = java.net.URLDecoder.decode(encodedUrl, "UTF-8")
            val textUsed = java.net.URLDecoder.decode(encodedText, "UTF-8")

            FeedbackScreen(
                audioUrl = audioUrl,
                textUsed = textUsed,
                modelType = modelType,
                isGuest = false,
                userId = currentUserId,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onLogoutClick = {
                    coroutineScope.launch { sessionManager.clearSession() }
                    navController.navigate(Screen.Landing.route) { popUpTo(0) }
                }
            )
        }

        composable(Screen.AdminDashboard.route) {
            AdminDashboardScreen(
                onLogoutClick = {
                    coroutineScope.launch { sessionManager.clearSession() }
                    navController.navigate(Screen.Landing.route) { popUpTo(0) }
                }
            )
        }
    }
}