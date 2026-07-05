package com.example.echoro.network

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val full_name: String,
    val email: String,
    val password: String
)

data class User(
    val id: Int,
    val full_name: String,
    val email: String,
    val role: String
)

data class AuthResponse(
    val status: String,
    val message: String?,
    val user: User?,
    val access_token: String? = null
)

data class GenerateRequest(
    val text: String,
    val description: String,
    val model_type: String
)

data class GenerateResponse(
    val status: String,
    val audio_url: String,
    val text_used: String
)

data class FeedbackRequest(
    val audio_url: String,
    val model_type: String,
    val intelligibility: Int,
    val naturalness: Int,
    val accent: Int,
    val word_accuracy: Float,
    val gender_respected: Boolean,
    val comments: String
)

data class FeedbackResponse(
    val status: String,
    val message: String
)

data class ModelStats(
    val intelligibility: Float,
    val naturalness: Float,
    val accent: Float,
    val word_accuracy: Float,
    val gender_match: Float
)

data class TrendData(
    val dates: List<String>,
    val eagle: List<Float>,
    val wolf: List<Float>,
    val reindeer: List<Float>,
    val sparrow: List<Float>
)

data class OverviewResponse(
    val status: String,
    val total_generations: Int,
    val overall_mos: Float
)

data class ModelsStatsResponse(
    val status: String,
    val models: Map<String, ModelStats>
)

data class TrendResponse(
    val status: String,
    val trend: TrendData
)

data class ABTestPageResult(
    val page_index: Int,
    val model_a: String,
    val model_b: String,
    val naturalness: String,
    val intelligibility: String,
    val accent: String,
    val word_accuracy: String
)

data class ABTestRequest(
    val total_count: Int,
    val results: List<ABTestPageResult>
)

data class ABTestResponse(
    val status: String,
    val message: String,
    val session_id: Int? = null
)

data class ABTestAggregateStats(
    val total_results: Int,
    val naturalness_voice_a_pct: Float,
    val naturalness_voice_b_pct: Float,
    val naturalness_equal_pct: Float,
    val intelligibility_voice_a_pct: Float,
    val intelligibility_voice_b_pct: Float,
    val intelligibility_equal_pct: Float,
    val accent_voice_a_pct: Float,
    val accent_voice_b_pct: Float,
    val accent_equal_pct: Float,
    val word_accuracy_voice_a_pct: Float,
    val word_accuracy_voice_b_pct: Float,
    val word_accuracy_equal_pct: Float
)

data class ABTestStatsResponse(
    val status: String,
    val stats: ABTestAggregateStats
)

data class ModelRanking(
    val model: String,
    val elo: Float,
    val ci_low: Float,
    val ci_high: Float
)

data class MeasureRankingData(
    val rankings: List<ModelRanking>,
    val win_rates: Map<String, Map<String, Float>>,
    val significance: Map<String, Map<String, Boolean>>
)

data class ABRankingsResponse(
    val status: String,
    val total_trials: Int,
    val measures: Map<String, MeasureRankingData>
)

data class ABTestSessionItem(
    val page_index: Int,
    val sentence_id: String,
    val audio_url_a: String,
    val audio_url_b: String,
    val text: String,
    val description: String
)

data class ABTestSessionResponse(
    val model_a: String,
    val model_b: String,
    val total_count: Int,
    val items: List<ABTestSessionItem>
)

interface EchoRoApi {
    @POST("login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @POST("generate-voice")
    suspend fun generateVoice(@Body request: GenerateRequest): GenerateResponse

    @POST("feedback")
    suspend fun submitFeedback(@Body request: FeedbackRequest): FeedbackResponse

    @GET("admin/stats/overview")
    suspend fun getOverviewStats(): OverviewResponse

    @GET("admin/stats/models")
    suspend fun getModelsStats(): ModelsStatsResponse

    @GET("admin/stats/trend")
    suspend fun getTrendStats(
        @Query("start_date") startDate: String? = null,
        @Query("end_date") endDate: String? = null
    ): TrendResponse

    @POST("ab-test")
    suspend fun submitABTest(@Body request: ABTestRequest): ABTestResponse

    @GET("ab-test/session")
    suspend fun getABTestSession(@Query("count") count: Int): ABTestSessionResponse

    @GET("admin/ab-test/stats")
    suspend fun getABTestStats(): ABTestStatsResponse

    @GET("admin/ab-test/rankings")
    suspend fun getABRankings(): ABRankingsResponse
}

object TokenStore {
    var token: String? = null
}

object RetrofitClient {
    // 10.0.0.2
    const val BASE_URL = "http://192.168.10.81:8000/"

    private class AuthInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val request = chain.request()
            val token = TokenStore.token
            return if (token != null) {
                chain.proceed(
                    request.newBuilder()
                        .addHeader("Authorization", "Bearer $token")
                        .build()
                )
            } else {
                chain.proceed(request)
            }
        }
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(AuthInterceptor())
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(1500, TimeUnit.SECONDS)
        .writeTimeout(1500, TimeUnit.SECONDS)
        .build()

    val instance: EchoRoApi by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(EchoRoApi::class.java)
    }
}