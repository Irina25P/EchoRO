package com.example.echoro.viewmodel.auth

import android.app.Application
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.echoro.R
import com.example.echoro.viewmodel.Resource
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel(private val app: Application) : AndroidViewModel(app) {

    private val repository = AuthRepository()

    private val _authState = MutableStateFlow(AuthScreenStateHolder())
    val authState: StateFlow<AuthScreenStateHolder> = _authState.asStateFlow()

    private val _authOSE = Channel<AuthScreenOSE>()
    val authOSE = _authOSE.receiveAsFlow()

    fun sendEvent(event: AuthScreenEvent) {
        when (event) {
            is AuthScreenEvent.EmailChanged -> {
                _authState.update { it.copy(emailError = null, generalError = null) }
            }
            is AuthScreenEvent.PasswordChanged -> {
                _authState.update { it.copy(passwordError = null, generalError = null) }
            }
            is AuthScreenEvent.FullNameChanged -> {
                _authState.update { it.copy(fullNameError = null, generalError = null) }
            }
            is AuthScreenEvent.ConfirmPasswordChanged -> {
                _authState.update { it.copy(confirmPasswordError = null, generalError = null) }
            }
            is AuthScreenEvent.SignInWithCredentials -> handleSignIn(event)
            is AuthScreenEvent.SignUpWithCredentials -> handleSignUp(event)
        }
    }

    private fun handleSignIn(event: AuthScreenEvent.SignInWithCredentials) {
        var hasError = false
        _authState.update { it.clearFieldErrors() }

        if (event.email.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(event.email).matches()) {
            _authState.update { it.copy(emailError = app.getString(R.string.error_invalid_email)) }
            hasError = true
        }
        if (event.password.isBlank()) {
            _authState.update { it.copy(passwordError = app.getString(R.string.error_password_required)) }
            hasError = true
        }

        if (hasError) return

        viewModelScope.launch {
            repository.login(event.email, event.password).collectLatest { result ->
                when (result) {
                    is Resource.Loading -> {
                        _authState.update { it.showLoading() }
                    }
                    is Resource.Success -> {
                        _authState.update { it.hideLoading() }
                        val userId = result.data.id
                        val userRole = result.data.role
                        emitOSE(AuthScreenOSE.NavigateToApp(userId, userRole))
                    }
                    is Resource.Error -> {
                        _authState.update { it.showError(result.exception.message ?: app.getString(R.string.error_authentication)) }
                        emitOSE(AuthScreenOSE.Error(result.exception.message ?: app.getString(R.string.error_generic)))
                    }
                }
            }
        }
    }

    private fun handleSignUp(event: AuthScreenEvent.SignUpWithCredentials) {
        var hasError = false
        _authState.update { it.clearFieldErrors() }

        if (event.fullName.isBlank()) {
            _authState.update { it.copy(fullNameError = app.getString(R.string.error_full_name_required)) }
            hasError = true
        }
        if (event.email.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(event.email).matches()) {
            _authState.update { it.copy(emailError = app.getString(R.string.error_invalid_email)) }
            hasError = true
        }
        if (event.password.length < 6) {
            _authState.update { it.copy(passwordError = app.getString(R.string.error_password_too_short)) }
            hasError = true
        }
        if (event.confirmPassword != event.password) {
            _authState.update { it.copy(confirmPasswordError = app.getString(R.string.error_password_mismatch)) }
            hasError = true
        }

        if (hasError) return

        viewModelScope.launch {
            repository.register(event.fullName, event.email, event.password).collectLatest { result ->
                when (result) {
                    is Resource.Loading -> {
                        _authState.update { it.showLoading() }
                    }
                    is Resource.Success -> {
                        _authState.update { it.hideLoading() }
                        val userId = result.data.id
                        val userRole = result.data.role
                        emitOSE(AuthScreenOSE.NavigateToApp(userId, userRole))
                    }
                    is Resource.Error -> {
                        _authState.update { it.showError(result.exception.message ?: app.getString(R.string.error_registration)) }
                        emitOSE(AuthScreenOSE.Error(result.exception.message ?: app.getString(R.string.error_generic)))
                    }
                }
            }
        }
    }

    private fun emitOSE(ose: AuthScreenOSE) {
        viewModelScope.launch { _authOSE.send(ose) }
    }
}