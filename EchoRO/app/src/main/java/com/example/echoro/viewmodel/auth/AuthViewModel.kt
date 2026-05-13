package com.example.echoro.viewmodel.auth

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.echoro.viewmodel.Resource
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {

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
            _authState.update { it.copy(emailError = "Adresă de email invalidă.") }
            hasError = true
        }
        if (event.password.isBlank()) {
            _authState.update { it.copy(passwordError = "Parola este obligatorie.") }
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
                        _authState.update { it.showError(result.exception.message ?: "Eroare la autentificare") }
                        emitOSE(AuthScreenOSE.Error(result.exception.message ?: "Eroare"))
                    }
                }
            }
        }
    }

    private fun handleSignUp(event: AuthScreenEvent.SignUpWithCredentials) {
        var hasError = false
        _authState.update { it.clearFieldErrors() }

        if (event.fullName.isBlank()) {
            _authState.update { it.copy(fullNameError = "Numele complet este obligatoriu.") }
            hasError = true
        }
        if (event.email.isBlank() || !Patterns.EMAIL_ADDRESS.matcher(event.email).matches()) {
            _authState.update { it.copy(emailError = "Adresă de email invalidă.") }
            hasError = true
        }
        if (event.password.length < 6) {
            _authState.update { it.copy(passwordError = "Parola trebuie să aibă minim 6 caractere.") }
            hasError = true
        }
        if (event.confirmPassword != event.password) {
            _authState.update { it.copy(confirmPasswordError = "Parolele nu se potrivesc.") }
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
                        _authState.update { it.showError(result.exception.message ?: "Eroare la înregistrare") }
                        emitOSE(AuthScreenOSE.Error(result.exception.message ?: "Eroare"))
                    }
                }
            }
        }
    }

    private fun emitOSE(ose: AuthScreenOSE) {
        viewModelScope.launch { _authOSE.send(ose) }
    }
}