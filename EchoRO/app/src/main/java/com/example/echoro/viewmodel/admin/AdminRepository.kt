package com.example.echoro.viewmodel.admin

import com.example.echoro.network.ABRankingsResponse
import com.example.echoro.network.ABTestStatsResponse
import com.example.echoro.network.ModelsStatsResponse
import com.example.echoro.network.OverviewResponse
import com.example.echoro.network.RetrofitClient
import com.example.echoro.network.TrendResponse


class AdminRepository {
    suspend fun getOverviewStats(): OverviewResponse {
        return RetrofitClient.instance.getOverviewStats()
    }

    suspend fun getModelsStats(): ModelsStatsResponse {
        return RetrofitClient.instance.getModelsStats()
    }

    suspend fun getTrendStats(startDate: String?, endDate: String?): TrendResponse {
        return RetrofitClient.instance.getTrendStats(startDate, endDate)
    }

    suspend fun getABTestStats(): ABTestStatsResponse {
        return RetrofitClient.instance.getABTestStats()
    }

    suspend fun getABRankings(): ABRankingsResponse {
        return RetrofitClient.instance.getABRankings()
    }
}