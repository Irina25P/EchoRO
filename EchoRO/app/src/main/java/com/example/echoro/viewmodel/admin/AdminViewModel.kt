package com.example.echoro.viewmodel.admin

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
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AdminViewModel : ViewModel() {
    private val repository = AdminRepository()

    private val _state = MutableStateFlow(AdminStateHolder())
    val state: StateFlow<AdminStateHolder> = _state.asStateFlow()

    private val _ose = Channel<AdminOSE>()
    val ose = _ose.receiveAsFlow()

    init {
        loadDefault7DaysStats()
    }

    fun sendEvent(event: AdminEvent) {
        when (event) {
            is AdminEvent.LoadStats -> {
                _state.update { it.showTrendLoading() }
                fetchStats(event.startDate, event.endDate)
            }
        }
    }

    private fun loadDefault7DaysStats() {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val calendar = Calendar.getInstance()

        val endDate = formatter.format(calendar.time) // Azi

        calendar.add(Calendar.DAY_OF_YEAR, -7)
        val startDate = formatter.format(calendar.time) // Acum 7 zile

        fetchStats(startDate, endDate)
    }

    private fun fetchStats(startDate: String?, endDate: String?) {
        viewModelScope.launch {
            repository.getAdminStats(startDate, endDate).collectLatest { result ->
                when (result) {
                    is Resource.Loading -> {
                    }
                    is Resource.Success -> {
                        _state.update { it.success(result.data) }
                    }
                    is Resource.Error -> {
                        val errorMsg = result.exception.message ?: "Eroare la încărcarea statisticilor"
                        _state.update { it.showError(errorMsg) }
                        emitOSE(AdminOSE.ShowMessage(errorMsg))
                    }
                }
            }
        }
    }

    private fun emitOSE(ose: AdminOSE) {
        viewModelScope.launch { _ose.send(ose) }
    }
}