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

    val eagleIntelligibility: Float = 0f,
    val eagleNaturalness: Float = 0f,
    val eagleAccent: Float = 0f,
    val eagleWordAccuracy: Float = 0f,
    val eagleGenderMatch: Float = 0f,

    val wolfIntelligibility: Float = 0f,
    val wolfNaturalness: Float = 0f,
    val wolfAccent: Float = 0f,
    val wolfWordAccuracy: Float = 0f,
    val wolfGenderMatch: Float = 0f,

    val reindeerIntelligibility: Float = 0f,
    val reindeerNaturalness: Float = 0f,
    val reindeerAccent: Float = 0f,
    val reindeerWordAccuracy: Float = 0f,
    val reindeerGenderMatch: Float = 0f,

    val sparrowIntelligibility: Float = 0f,
    val sparrowNaturalness: Float = 0f,
    val sparrowAccent: Float = 0f,
    val sparrowWordAccuracy: Float = 0f,
    val sparrowGenderMatch: Float = 0f,

    val trendDates: List<String> = emptyList(),
    val trendEagle: List<Float> = emptyList(),
    val trendWolf: List<Float> = emptyList(),
    val trendReindeer: List<Float> = emptyList(),
    val trendSparrow: List<Float> = emptyList(),

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
        val eagle = modelsRes.models["Eagle"]
        val wolf = modelsRes.models["Wolf"]
        val reindeer = modelsRes.models["Reindeer"]
        val sparrow = modelsRes.models["Sparrow"]
        val ab = abStatsRes.stats

        return this.copy(
            isInitialLoading = false,
            isTrendLoading = false,

            totalGenerations = "%,d".format(overview.total_generations),
            overallMos = overview.overall_mos.toString(),

            eagleIntelligibility = eagle?.intelligibility ?: 0f,
            eagleNaturalness = eagle?.naturalness ?: 0f,
            eagleAccent = eagle?.accent ?: 0f,
            eagleWordAccuracy = eagle?.word_accuracy ?: 0f,
            eagleGenderMatch = eagle?.gender_match ?: 0f,

            wolfIntelligibility = wolf?.intelligibility ?: 0f,
            wolfNaturalness = wolf?.naturalness ?: 0f,
            wolfAccent = wolf?.accent ?: 0f,
            wolfWordAccuracy = wolf?.word_accuracy ?: 0f,
            wolfGenderMatch = wolf?.gender_match ?: 0f,

            reindeerIntelligibility = reindeer?.intelligibility ?: 0f,
            reindeerNaturalness = reindeer?.naturalness ?: 0f,
            reindeerAccent = reindeer?.accent ?: 0f,
            reindeerWordAccuracy = reindeer?.word_accuracy ?: 0f,
            reindeerGenderMatch = reindeer?.gender_match ?: 0f,

            sparrowIntelligibility = sparrow?.intelligibility ?: 0f,
            sparrowNaturalness = sparrow?.naturalness ?: 0f,
            sparrowAccent = sparrow?.accent ?: 0f,
            sparrowWordAccuracy = sparrow?.word_accuracy ?: 0f,
            sparrowGenderMatch = sparrow?.gender_match ?: 0f,

            trendDates = trendRes.trend.dates,
            trendEagle = trendRes.trend.eagle,
            trendWolf = trendRes.trend.wolf,
            trendReindeer = trendRes.trend.reindeer,
            trendSparrow = trendRes.trend.sparrow,

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
            trendEagle = trendRes.trend.eagle,
            trendWolf = trendRes.trend.wolf,
            trendReindeer = trendRes.trend.reindeer,
            trendSparrow = trendRes.trend.sparrow
        )
    }
}
