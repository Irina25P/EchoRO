package com.example.echoro.viewmodel.auth

data class AuthScreenStateHolder(
    val isLoading: Boolean = false,
    val generalError: String? = null,

    val fullNameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null
) {
    fun showLoading(): AuthScreenStateHolder =
        this.copy(isLoading = true, generalError = null)

    fun hideLoading(): AuthScreenStateHolder =
        this.copy(isLoading = false)

    fun showError(message: String): AuthScreenStateHolder =
        this.copy(isLoading = false, generalError = message)

    fun clearFieldErrors(): AuthScreenStateHolder =
        this.copy(
            fullNameError = null,
            emailError = null,
            passwordError = null,
            confirmPasswordError = null
        )
}