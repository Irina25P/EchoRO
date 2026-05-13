package com.example.echoro.viewmodel.feedback
sealed class FeedbackEvent {
    data class SubmitClicked(
        val userId: Int,
        val audioUrl: String,
        val modelType: String,
        val intelligibility: Int,
        val naturalness: Int,
        val accent: Int,
        val wordAccuracy: Float,
        val genderRespected: Boolean,
        val comments: String
    ) : FeedbackEvent()
}