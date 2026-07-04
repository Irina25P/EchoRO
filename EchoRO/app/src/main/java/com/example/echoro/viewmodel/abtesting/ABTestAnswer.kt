package com.example.echoro.viewmodel.abtesting

import com.example.echoro.ui.screens.abtesting.PreferenceOption

data class ABTestAnswer(
    val pageIndex: Int,
    val modelA: String,
    val modelB: String,
    val naturalness: PreferenceOption,
    val intelligibility: PreferenceOption,
    val accent: PreferenceOption,
    val wordAccuracy: PreferenceOption
)
