package com.example.echoro.viewmodel.admin

import com.example.echoro.network.ABRankingsResponse
import com.example.echoro.network.ABTestStatsResponse
import com.example.echoro.network.MeasureRankingData
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
    val trendLarge: List<Float> = emptyList(),

    val abTotalResults: Int = 0,
    val abNaturalnessVoiceAPct: Float = 0f,
    val abNaturalnessVoiceBPct: Float = 0f,
    val abNaturalnessEqualPct: Float = 0f,
    val abIntelligibilityVoiceAPct: Float = 0f,
    val abIntelligibilityVoiceBPct: Float = 0f,
    val abIntelligibilityEqualPct: Float = 0f,
    val abAccentVoiceAPct: Float = 0f,
    val abAccentVoiceBPct: Float = 0f,
    val abAccentEqualPct: Float = 0f,
    val abWordAccuracyVoiceAPct: Float = 0f,
    val abWordAccuracyVoiceBPct: Float = 0f,
    val abWordAccuracyEqualPct: Float = 0f,

    // Bradley-Terry rankings per measure
    val abTotalTrials: Int = 0,
    val abRankings: Map<String, MeasureRankingData> = emptyMap()
) {
    fun showInitialLoading(): AdminStateHolder = this.copy(isInitialLoading = true, error = null)

    fun showTrendLoading(): AdminStateHolder = this.copy(isTrendLoading = true, error = null)

    fun showError(msg: String): AdminStateHolder = this.copy(isInitialLoading = false, isTrendLoading = false, error = msg)

    fun successAll(
        overview: OverviewResponse,
        modelsRes: ModelsStatsResponse,
        trendRes: TrendResponse,
        abStatsRes: ABTestStatsResponse,
        abRankingsRes: ABRankingsResponse
    ): AdminStateHolder {
        val mini = modelsRes.models["Mini"]
        val large = modelsRes.models["Large"]
        val ab = abStatsRes.stats

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
            trendLarge = trendRes.trend.large,

            abTotalResults = ab.total_results,
            abNaturalnessVoiceAPct = ab.naturalness_voice_a_pct,
            abNaturalnessVoiceBPct = ab.naturalness_voice_b_pct,
            abNaturalnessEqualPct = ab.naturalness_equal_pct,
            abIntelligibilityVoiceAPct = ab.intelligibility_voice_a_pct,
            abIntelligibilityVoiceBPct = ab.intelligibility_voice_b_pct,
            abIntelligibilityEqualPct = ab.intelligibility_equal_pct,
            abAccentVoiceAPct = ab.accent_voice_a_pct,
            abAccentVoiceBPct = ab.accent_voice_b_pct,
            abAccentEqualPct = ab.accent_equal_pct,
            abWordAccuracyVoiceAPct = ab.word_accuracy_voice_a_pct,
            abWordAccuracyVoiceBPct = ab.word_accuracy_voice_b_pct,
            abWordAccuracyEqualPct = ab.word_accuracy_equal_pct,

            abTotalTrials = abRankingsRes.total_trials,
            abRankings = abRankingsRes.measures
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
