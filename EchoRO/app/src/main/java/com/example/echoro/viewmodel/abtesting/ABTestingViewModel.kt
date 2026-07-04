package com.example.echoro.viewmodel.abtesting

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
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

class ABTestingViewModel(private val app: Application, totalCount: Int) : AndroidViewModel(app) {

    private val repository = ABTestingRepository()

    private val _state = MutableStateFlow(ABTestingState(totalCount = totalCount))
    val state: StateFlow<ABTestingState> = _state.asStateFlow()

    private val _ose = Channel<ABTestingOSE>()
    val ose = _ose.receiveAsFlow()

    fun sendEvent(event: ABTestingEvent) {
        when (event) {
            is ABTestingEvent.SaveAnswer -> handleSaveAnswer(event)
            is ABTestingEvent.Next -> handleNext()
            is ABTestingEvent.Submit -> handleSubmit()
        }
    }

    private fun handleSaveAnswer(event: ABTestingEvent.SaveAnswer) {
        val current = _state.value
        val answer = ABTestAnswer(
            pageIndex = current.currentPage,
            modelA = event.modelA,
            modelB = event.modelB,
            naturalness = event.naturalness,
            intelligibility = event.intelligibility,
            accent = event.accent,
            wordAccuracy = event.wordAccuracy
        )
        _state.update { it.copy(answers = it.answers + (current.currentPage to answer)) }
    }

    private fun handleNext() {
        val current = _state.value
        if (current.currentPage < current.totalCount - 1) {
            _state.update { it.copy(currentPage = it.currentPage + 1) }
        }
    }

    private fun handleSubmit() {
        viewModelScope.launch {
            val current = _state.value
            repository.submitSession(current.totalCount, current.answers).collectLatest { result ->
                when (result) {
                    is Resource.Loading -> _state.update { it.copy(isLoading = true, error = null) }
                    is Resource.Success -> {
                        _state.update { it.copy(isLoading = false, isSubmitted = true) }
                        emitOSE(ABTestingOSE.ShowMessage(app.getString(R.string.ab_test_success)))
                        emitOSE(ABTestingOSE.NavigateBack)
                    }
                    is Resource.Error -> {
                        _state.update { it.copy(isLoading = false, error = result.exception.message) }
                        emitOSE(ABTestingOSE.ShowMessage(result.exception.message ?: app.getString(R.string.error_generic_message)))
                    }
                }
            }
        }
    }

    private fun emitOSE(ose: ABTestingOSE) {
        viewModelScope.launch { _ose.send(ose) }
    }

    class Factory(private val application: Application, private val totalCount: Int) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
            return ABTestingViewModel(application, totalCount) as T
        }
    }
}
