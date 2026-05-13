package com.example.echoro.viewmodel.generateVoice

data class GenerateScreenStateHolder(
    val isLoading: Boolean = false,
    val textError: String? = null,
    val generatedAudioUrl: String? = null,
    val textUsed: String? = null
) {
    fun showLoading(): GenerateScreenStateHolder =
        this.copy(
            isLoading = true,
            textError = null,
            generatedAudioUrl = null,
            textUsed = null
        )

    fun hideLoading(): GenerateScreenStateHolder =
        this.copy(isLoading = false)

    fun showError(error: String): GenerateScreenStateHolder =
        this.copy(isLoading = false, textError = error)

    fun success(audioUrl: String, textUsed: String): GenerateScreenStateHolder =
        this.copy(
            isLoading = false,
            generatedAudioUrl = audioUrl,
            textUsed = textUsed
        )
}