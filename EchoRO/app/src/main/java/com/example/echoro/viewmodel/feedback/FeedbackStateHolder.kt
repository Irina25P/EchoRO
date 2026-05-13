package com.example.echoro.viewmodel.feedback

data class FeedbackStateHolder(
    val isLoading: Boolean = false,
    val error: String? = null
) {
    fun showLoading(): FeedbackStateHolder =
        this.copy(isLoading = true, error = null)

    fun hideLoading(): FeedbackStateHolder =
        this.copy(isLoading = false)

    fun showError(msg: String): FeedbackStateHolder =
        this.copy(isLoading = false, error = msg)
}