package com.example.echoro.viewmodel.feedback

import com.example.echoro.network.FeedbackRequest
import com.example.echoro.network.FeedbackResponse
import com.example.echoro.network.RetrofitClient
import com.example.echoro.viewmodel.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class FeedbackRepository {
    suspend fun submitFeedback(request: FeedbackRequest): Flow<Resource<FeedbackResponse>> = flow {
        emit(Resource.Loading)
        try {
            val response = RetrofitClient.instance.submitFeedback(request)
            if (response.status == "success") {
                emit(Resource.Success(response))
            } else {
                emit(Resource.Error(Exception(response.message ?: "Eroare la salvarea feedback-ului.")))
            }
        } catch (e: Exception) {
            emit(Resource.Error(Exception(e.message ?: "Eroare de conexiune la server.")))
        }
    }
}