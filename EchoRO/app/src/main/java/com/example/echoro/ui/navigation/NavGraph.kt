package com.example.echoro.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.echoro.ui.screens.admin.AdminDashboardScreen
import com.example.echoro.ui.screens.feedback.FeedbackScreen
import com.example.echoro.ui.screens.generatevoice.GenerateVoiceScreen
import com.example.echoro.ui.screens.generatevoice.LoadingScreen
import com.example.echoro.ui.screens.landingpage.LandingScreen
import com.example.echoro.ui.screens.login.LoginScreen
import com.example.echoro.ui.screens.login.RegisterScreen
import com.example.echoro.ui.screens.splash.SplashScreen
import kotlinx.coroutines.delay

@Composable
fun NavGraph() {
    val navController = rememberNavController()

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
                    navController.navigate(Screen.GenerateVoice.createRoute(isGuest = true))
                }
            )
        }

        composable(Screen.Login.route) {
            LoginScreen(
                onLoginClick = { email, password ->
                    if (email == "admin@admin.com") {
                        navController.navigate(Screen.AdminDashboard.route) {
                            popUpTo(Screen.Landing.route) { inclusive = true }
                        }
                    } else {
                        navController.navigate(Screen.GenerateVoice.createRoute(isGuest = false)) {
                            popUpTo(Screen.Landing.route) { inclusive = true }
                        }
                    }
                },
                onCreateAccountClick = { navController.navigate(Screen.Register.route) }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterClick = { fullName, email, password ->
                    navController.navigate(Screen.GenerateVoice.createRoute(isGuest = false)) {
                        popUpTo(Screen.Landing.route) { inclusive = true }
                    }
                },
                onLoginClick = { navController.popBackStack() },
                onContinueAsGuest = {
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
                onGenerateClick = { text ->
                    navController.navigate(Screen.Feedback.route)
                },
                onLoginClick = { navController.navigate(Screen.Login.route) },
                onLogoutClick = {
                    navController.navigate(Screen.Landing.route) {
                        popUpTo(0)
                    }
                }
            )
        }

        composable(Screen.Feedback.route) {
            FeedbackScreen(
                isGuest = false,
                onSubmitFeedback = { intelligibility, naturalness, accent, accuracy, comments ->
                    navController.popBackStack()
                },
                onLogoutClick = {
                    navController.navigate(Screen.Landing.route) { popUpTo(0) }
                }
            )
        }

        composable(Screen.AdminDashboard.route) {
            AdminDashboardScreen(
                onLogoutClick = {
                    navController.navigate(Screen.Landing.route) { popUpTo(0) }
                }
            )
        }
    }
}