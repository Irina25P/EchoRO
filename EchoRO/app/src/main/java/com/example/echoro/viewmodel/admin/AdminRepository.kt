package com.example.echoro.viewmodel.admin

import com.example.echoro.network.AdminStatsResponse
import com.example.echoro.network.RetrofitClient
import com.example.echoro.viewmodel.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AdminRepository {
    suspend fun getAdminStats(startDate: String?, endDate: String?): Flow<Resource<AdminStatsResponse>> =
        flow {
            emit(Resource.Loading)
            try {
                val response = RetrofitClient.instance.getAdminStats(startDate, endDate)
                if (response.status == "success") {
                    emit(Resource.Success(response))
                } else {
                    emit(Resource.Error(Exception("Eroare la preluarea statisticilor.")))
                }
            } catch (e: Exception) {
                emit(Resource.Error(Exception(e.message ?: "Eroare de conexiune la server.")))
            }
        }
}