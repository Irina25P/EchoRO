package com.example.echoro.viewmodel.feedback

sealed class FeedbackOSE {
    object None : FeedbackOSE()
    data class ShowMessage(val message: String) : FeedbackOSE()
    object NavigateBack : FeedbackOSE()
}