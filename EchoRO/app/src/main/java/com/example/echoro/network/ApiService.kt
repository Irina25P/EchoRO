package com.example.echoro.network

import okhttp3.OkHttpClient
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
    val user: User?
)

data class GenerateRequest(
    val user_id: Int,
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
    val user_id: Int,
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

data class AdminStatsResponse(
    val status: String,
    val total_generations: Int,
    val overall_mos: Float,
    val models: Map<String, ModelStats>,
    val trend: TrendData
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
    val mini: List<Float>,
    val large: List<Float>
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

    @GET("admin/stats")
    suspend fun getAdminStats(
        @Query("start_date") startDate: String? = null,
        @Query("end_date") endDate: String? = null
    ): AdminStatsResponse
}

object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:8000/"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(300, TimeUnit.SECONDS)
        .writeTimeout(300, TimeUnit.SECONDS)
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