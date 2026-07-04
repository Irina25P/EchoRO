package com.example.echoro.viewmodel.abtesting

val AB_PAIRS: List<Pair<String, String>> = listOf(
    Pair("Eagle", "Reindeer"),
    Pair("Eagle", "Sparrow"),
    Pair("Eagle", "Wolf"),
    Pair("Reindeer", "Sparrow"),
    Pair("Reindeer", "Wolf"),
    Pair("Sparrow", "Wolf")
)

data class ABTestingState(
    val totalCount: Int = 10,
    val currentPage: Int = 0,
    val answers: Map<Int, ABTestAnswer> = emptyMap(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSubmitted: Boolean = false
) {
    val isLastPage: Boolean get() = currentPage == totalCount - 1
    val currentAnswer: ABTestAnswer? get() = answers[currentPage]
    val isCurrentPageComplete: Boolean
        get() = currentAnswer != null
    val currentPair: Pair<String, String>
        get() = AB_PAIRS[currentPage % AB_PAIRS.size]
}
