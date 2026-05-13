package com.example.echoro.viewmodel.generateVoice

sealed class GenerateScreenEvent {
    data class TextChanged(val text: String) : GenerateScreenEvent()

    data class GenerateClicked(
        val userId: Int,
        val text: String,
        val description: String,
        val modelType: String
    ) : GenerateScreenEvent()
}