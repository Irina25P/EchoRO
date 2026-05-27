package com.example.echoro.viewmodel.admin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
        loadInitialData()
    }

    fun sendEvent(event: AdminEvent) {
        when (event) {
            is AdminEvent.LoadStats -> {
                fetchTrendOnly(event.startDate, event.endDate)
            }
        }
    }

    private fun getDefaultDateRange(): Pair<String, String> {
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val calendar = Calendar.getInstance()

        val endDate = formatter.format(calendar.time)
        calendar.add(Calendar.DAY_OF_YEAR, -7)
        val startDate = formatter.format(calendar.time)

        return Pair(startDate, endDate)
    }

    private fun loadInitialData() {
        val (startDate, endDate) = getDefaultDateRange()

        viewModelScope.launch {
            _state.update { it.showInitialLoading() }

            try {
                val overviewDeferred = async { repository.getOverviewStats() }
                val modelsDeferred = async { repository.getModelsStats() }
                val trendDeferred = async { repository.getTrendStats(startDate, endDate) }

                val overview = overviewDeferred.await()
                val models = modelsDeferred.await()
                val trend = trendDeferred.await()

                _state.update { it.successAll(overview, models, trend) }

            } catch (e: Exception) {
                val errorMsg = e.message ?: "Eroare la încărcarea statisticilor inițiale"
                _state.update { it.showError(errorMsg) }
                emitOSE(AdminOSE.ShowMessage(errorMsg))
            }
        }
    }

    private fun fetchTrendOnly(startDate: String?, endDate: String?) {
        viewModelScope.launch {
            _state.update { it.showTrendLoading() }

            try {
                val trend = repository.getTrendStats(startDate, endDate)
                _state.update { it.successTrend(trend) }

            } catch (e: Exception) {
                val errorMsg = e.message ?: "Eroare la încărcarea graficului de trend"
                _state.update { it.showError(errorMsg) }
                emitOSE(AdminOSE.ShowMessage(errorMsg))
            }
        }
    }

    private fun emitOSE(ose: AdminOSE) {
        viewModelScope.launch { _ose.send(ose) }
    }
}