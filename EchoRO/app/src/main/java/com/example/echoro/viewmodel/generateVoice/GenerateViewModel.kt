package com.example.echoro.ui.screens.generate

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.echoro.R
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

class GenerateViewModel(private val app: Application) : AndroidViewModel(app) {
    private val repository = GenerateRepository()

    private val _state = MutableStateFlow(GenerateScreenStateHolder())
    val state: StateFlow<GenerateScreenStateHolder> = _state.asStateFlow()

    private val _ose = Channel<GenerateScreenOSE>(Channel.BUFFERED)
    val ose = _ose.receiveAsFlow()

    fun sendEvent(event: GenerateScreenEvent) {
        when (event) {
            is GenerateScreenEvent.TextChanged -> {
                _state.update { it.copy(textError = null) }
            }
            is GenerateScreenEvent.GenerateClicked -> {
                handleGeneration(event.text, event.description, event.modelType)
            }
        }
    }

    private fun handleGeneration(text: String, description: String, modelType: String) {
        if (text.isBlank()) {
            _state.update { it.showError(app.getString(R.string.error_empty_text)) }
            return
        }

        viewModelScope.launch {
            repository.generateVoice(text, description, modelType).collectLatest { result ->
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
                        Log.e("GenerateVoice", "Eroare reală Retrofit: ", result.exception)
                        emitOSE(GenerateScreenOSE.ShowError(result.exception.message ?: app.getString(R.string.error_generation_failed)))
                    }
                }
            }
        }
    }

    private fun emitOSE(ose: GenerateScreenOSE) {
        viewModelScope.launch { _ose.send(ose) }
    }
}