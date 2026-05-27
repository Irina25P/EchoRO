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
    val mini: List<Float>,
    val large: List<Float>
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
}

object TokenStore {
    var token: String? = null
}

object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:8000/"

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