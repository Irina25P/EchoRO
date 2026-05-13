package com.example.echoro.viewmodel.auth

sealed class AuthScreenEvent {
    data class EmailChanged(val email: String) : AuthScreenEvent()
    data class PasswordChanged(val password: String) : AuthScreenEvent()
    data class FullNameChanged(val fullName: String) : AuthScreenEvent()
    data class ConfirmPasswordChanged(val password: String) : AuthScreenEvent()

    data class SignInWithCredentials(
        val email: String,
        val password: String
    ) : AuthScreenEvent()

    data class SignUpWithCredentials(
        val fullName: String,
        val email: String,
        val password: String,
        val confirmPassword: String
    ) : AuthScreenEvent()
}