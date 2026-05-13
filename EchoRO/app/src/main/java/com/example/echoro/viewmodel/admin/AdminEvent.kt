package com.example.echoro.viewmodel.admin

sealed class AdminEvent {
    data class LoadStats(
        val startDate: String? = null,
        val endDate: String? = null
    ) : AdminEvent()
}