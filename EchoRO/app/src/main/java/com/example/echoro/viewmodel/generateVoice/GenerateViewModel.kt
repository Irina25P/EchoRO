package com.example.echoro.ui.screens.generate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.echoro.viewmodel.Resource
import com.example.echoro.viewmodel.generateVoice.GenerateRepository
import com.example.echoro.viewmodel.generateVoice.GenerateScreenEvent
import com.example.echoro.viewmodel.generateVoice.GenerateScreenOSE
import com.example.echoro.viewmodel.generateVoice.GenerateScreenStateHolder
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GenerateViewModel : ViewModel() {
    private val repository = GenerateRepository()

    private val _state = MutableStateFlow(GenerateScreenStateHolder())
    val state: StateFlow<GenerateScreenStateHolder> = _state.asStateFlow()

    private val _ose = Channel<GenerateScreenOSE>()
    val ose = _ose.receiveAsFlow()

    fun sendEvent(event: GenerateScreenEvent) {
        when (event) {
            is GenerateScreenEvent.TextChanged -> {
                _state.update { it.copy(textError = null) }
            }
            is GenerateScreenEvent.GenerateClicked -> {
                handleGeneration(event.userId, event.text, event.description, event.modelType)
            }
        }
    }

    private fun handleGeneration(userId: Int, text: String, description: String, modelType: String) {
        if (text.isBlank()) {
            _state.update { it.showError("Textul nu poate fi gol.") }
            return
        }

        viewModelScope.launch {
            repository.generateVoice(userId, text, description, modelType).collectLatest { result ->
                when (result) {
                    is Resource.Loading -> {
                        _state.update { it.showLoading() }
                    }
                    is Resource.Success -> {
                        _state.update { it.hideLoading() }

                        emitOSE(
                            GenerateScreenOSE.NavigateToFeedback(
                                audioUrl = result.data.audio_url,
                                textUsed = result.data.text_used ?: "",
                                modelType = modelType
                            )
                        )
                    }
                    is Resource.Error -> {
                        _state.update { it.hideLoading() }
                        emitOSE(GenerateScreenOSE.ShowError(result.exception.message ?: "Eroare la generare"))
                    }
                }
            }
        }
    }

    private fun emitOSE(ose: GenerateScreenOSE) {
        viewModelScope.launch { _ose.send(ose) }
    }
}