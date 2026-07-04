package com.example.echoro.ui.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Landing : Screen("landing")
    object Login : Screen("login")
    object Register : Screen("register")

    object GenerateVoice : Screen("generate_voice/{isGuest}") {
        fun createRoute(isGuest: Boolean) = "generate_voice/$isGuest"
    }

    object Loading : Screen("loading")
    object Feedback : Screen("feedback")
    object ABTest : Screen("ab_test/{totalCount}") {
        fun createRoute(totalCount: Int) = "ab_test/$totalCount"
    }
    object AdminDashboard : Screen("admin_dashboard")
}