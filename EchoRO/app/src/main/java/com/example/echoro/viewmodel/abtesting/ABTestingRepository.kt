package com.example.echoro.viewmodel.abtesting

import com.example.echoro.network.ABTestPageResult
import com.example.echoro.network.ABTestRequest
import com.example.echoro.network.ABTestResponse
import com.example.echoro.network.RetrofitClient
import com.example.echoro.viewmodel.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class ABTestingRepository {
    suspend fun submitSession(
        totalCount: Int,
        answers: Map<Int, ABTestAnswer>
    ): Flow<Resource<ABTestResponse>> = flow {
        emit(Resource.Loading)
        try {
            val results = answers.values.map { answer ->
                ABTestPageResult(
                    page_index = answer.pageIndex,
                    model_a = answer.modelA,
                    model_b = answer.modelB,
                    naturalness = answer.naturalness.name,
                    intelligibility = answer.intelligibility.name,
                    accent = answer.accent.name,
                    word_accuracy = answer.wordAccuracy.name
                )
            }.sortedBy { it.page_index }

            val request = ABTestRequest(total_count = totalCount, results = results)
            val response = RetrofitClient.instance.submitABTest(request)
            if (response.status == "success") {
                emit(Resource.Success(response))
            } else {
                emit(Resource.Error(Exception(response.message)))
            }
        } catch (e: Exception) {
            emit(Resource.Error(Exception(e.message ?: "Eroare de conexiune la server.")))
        }
    }
}
