package com.example.echoro.viewmodel.abtesting

import com.example.echoro.ui.screens.abtesting.PreferenceOption

sealed class ABTestingEvent {
    data class SaveAnswer(
        val modelA: String,
        val modelB: String,
        val naturalness: PreferenceOption,
        val intelligibility: PreferenceOption,
        val accent: PreferenceOption,
        val wordAccuracy: PreferenceOption
    ) : ABTestingEvent()

    object Next : ABTestingEvent()
    object Submit : ABTestingEvent()
}
