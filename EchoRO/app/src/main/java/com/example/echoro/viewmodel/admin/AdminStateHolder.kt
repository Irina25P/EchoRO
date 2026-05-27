package com.example.echoro.viewmodel.admin

import com.example.echoro.network.ModelsStatsResponse
import com.example.echoro.network.OverviewResponse
import com.example.echoro.network.TrendResponse

data class AdminStateHolder(
    val isInitialLoading: Boolean = true,
    val isTrendLoading: Boolean = false,
    val error: String? = null,

    val totalGenerations: String = "0",
    val overallMos: String = "0.0",

    val miniIntelligibility: Float = 0f,
    val miniNaturalness: Float = 0f,
    val miniAccent: Float = 0f,
    val miniWordAccuracy: Float = 0f,
    val miniGenderMatch: Float = 0f,

    val largeIntelligibility: Float = 0f,
    val largeNaturalness: Float = 0f,
    val largeAccent: Float = 0f,
    val largeWordAccuracy: Float = 0f,
    val largeGenderMatch: Float = 0f,

    val trendDates: List<String> = emptyList(),
    val trendMini: List<Float> = emptyList(),
    val trendLarge: List<Float> = emptyList()
) {
    fun showInitialLoading(): AdminStateHolder = this.copy(isInitialLoading = true, error = null)

    fun showTrendLoading(): AdminStateHolder = this.copy(isTrendLoading = true, error = null)

    fun showError(msg: String): AdminStateHolder = this.copy(isInitialLoading = false, isTrendLoading = false, error = msg)

    fun successAll(overview: OverviewResponse, modelsRes: ModelsStatsResponse, trendRes: TrendResponse): AdminStateHolder {
        val mini = modelsRes.models["Mini"]
        val large = modelsRes.models["Large"]

        return this.copy(
            isInitialLoading = false,
            isTrendLoading = false,

            totalGenerations = "%,d".format(overview.total_generations),
            overallMos = overview.overall_mos.toString(),

            miniIntelligibility = mini?.intelligibility ?: 0f,
            miniNaturalness = mini?.naturalness ?: 0f,
            miniAccent = mini?.accent ?: 0f,
            miniWordAccuracy = mini?.word_accuracy ?: 0f,
            miniGenderMatch = mini?.gender_match ?: 0f,

            largeIntelligibility = large?.intelligibility ?: 0f,
            largeNaturalness = large?.naturalness ?: 0f,
            largeAccent = large?.accent ?: 0f,
            largeWordAccuracy = large?.word_accuracy ?: 0f,
            largeGenderMatch = large?.gender_match ?: 0f,

            trendDates = trendRes.trend.dates,
            trendMini = trendRes.trend.mini,
            trendLarge = trendRes.trend.large
        )
    }

    fun successTrend(trendRes: TrendResponse): AdminStateHolder {
        return this.copy(
            isTrendLoading = false,
            trendDates = trendRes.trend.dates,
            trendMini = trendRes.trend.mini,
            trendLarge = trendRes.trend.large
        )
    }
}