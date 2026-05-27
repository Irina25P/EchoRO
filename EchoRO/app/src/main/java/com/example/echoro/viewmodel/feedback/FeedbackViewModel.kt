package com.example.echoro.viewmodel.feedback

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.echoro.network.FeedbackRequest
import com.example.echoro.viewmodel.Resource
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FeedbackViewModel : ViewModel() {
    private val repository = FeedbackRepository()

    private val _state = MutableStateFlow(FeedbackStateHolder())
    val state: StateFlow<FeedbackStateHolder> = _state.asStateFlow()

    private val _ose = Channel<FeedbackOSE>()
    val ose = _ose.receiveAsFlow()

    fun sendEvent(event: FeedbackEvent) {
        when (event) {
            is FeedbackEvent.SubmitClicked -> handleSubmit(event)
        }
    }

    private fun handleSubmit(event: FeedbackEvent.SubmitClicked) {
        viewModelScope.launch {
            val request = FeedbackRequest(
                audio_url = event.audioUrl,
                model_type = event.modelType,
                intelligibility = event.intelligibility,
                naturalness = event.naturalness,
                accent = event.accent,
                word_accuracy = event.wordAccuracy,
                gender_respected = event.genderRespected,
                comments = event.comments
            )

            repository.submitFeedback(request).collectLatest { result ->
                when (result) {
                    is Resource.Loading -> {
                        _state.update { it.showLoading() }
                    }
                    is Resource.Success -> {
                        _state.update { it.hideLoading() }
                        emitOSE(FeedbackOSE.ShowMessage("Feedback salvat cu succes! Mulțumim!"))
                        emitOSE(FeedbackOSE.NavigateBack)
                    }
                    is Resource.Error -> {
                        _state.update { it.showError(result.exception.message ?: "Eroare") }
                        emitOSE(FeedbackOSE.ShowMessage(result.exception.message ?: "A apărut o eroare."))
                    }
                }
            }
        }
    }

    private fun emitOSE(ose: FeedbackOSE) {
        viewModelScope.launch { _ose.send(ose) }
    }
}