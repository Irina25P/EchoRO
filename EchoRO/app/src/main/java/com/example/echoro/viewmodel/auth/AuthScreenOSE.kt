package com.example.echoro.viewmodel.auth

sealed class AuthScreenOSE {
    object None : AuthScreenOSE()
    data class Error(val message: String) : AuthScreenOSE()
    data class NavigateToApp(val userId: Int, val role: String) : AuthScreenOSE()
}