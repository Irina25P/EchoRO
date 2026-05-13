package com.example.echoro.viewmodel.generateVoice

sealed class GenerateScreenOSE {
    object None : GenerateScreenOSE()
    data class ShowError(val message: String) : GenerateScreenOSE()
    data class NavigateToFeedback(val audioUrl: String, val textUsed: String, val modelType: String) : GenerateScreenOSE()
}