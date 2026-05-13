package com.example.echoro.viewmodel.generateVoice

import com.example.echoro.network.GenerateRequest
import com.example.echoro.network.GenerateResponse
import com.example.echoro.network.RetrofitClient
import com.example.echoro.viewmodel.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class GenerateRepository {
    suspend fun generateVoice(userId: Int, text: String, description: String, modelType: String): Flow<Resource<GenerateResponse>> = flow {
        emit(Resource.Loading)
        try {
            val response = RetrofitClient.instance.generateVoice(
                GenerateRequest(user_id = userId, text = text, description = description, model_type = modelType)
            )
            if (response.status == "success") {
                emit(Resource.Success(response))
            } else {
                emit(Resource.Error(Exception("Eroare necunoscută la generare.")))
            }
        } catch (e: Exception) {
            emit(Resource.Error(Exception(e.message ?: "Eroare de conexiune la server.")))
        }
    }
}