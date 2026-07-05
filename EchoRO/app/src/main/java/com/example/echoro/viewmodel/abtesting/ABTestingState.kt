package com.example.echoro.viewmodel.abtesting

import com.example.echoro.network.ABTestSessionItem

data class ABTestingState(
    val totalCount: Int = 10,
    val currentPage: Int = 0,
    val modelA: String = "Eagle",
    val modelB: String = "Reindeer",
    val items: List<ABTestSessionItem> = emptyList(),
    val answers: Map<Int, ABTestAnswer> = emptyMap(),
    val isSessionLoading: Boolean = true,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSubmitted: Boolean = false
) {
    private val currentItem: ABTestSessionItem?
        get() = items.getOrNull(currentPage)

    val currentAudioUrlA: String? get() = currentItem?.audio_url_a
    val currentAudioUrlB: String? get() = currentItem?.audio_url_b
    val currentText: String get() = currentItem?.text ?: ""
    val currentDescription: String get() = currentItem?.description ?: ""

    val isLastPage: Boolean get() = currentPage == totalCount - 1
    val currentAnswer: ABTestAnswer? get() = answers[currentPage]
    val isCurrentPageComplete: Boolean get() = currentAnswer != null
}
