package com.example.echoro.viewmodel.auth

import com.example.echoro.network.LoginRequest
import com.example.echoro.network.RegisterRequest
import com.example.echoro.network.RetrofitClient
import com.example.echoro.network.User
import com.example.echoro.viewmodel.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class AuthRepository {

    suspend fun login(email: String, password: String): Flow<Resource<User>> = flow {
        emit(Resource.Loading)
        try {
            val response = RetrofitClient.instance.login(LoginRequest(email, password))
            if (response.status == "success" && response.user != null) {
                emit(Resource.Success(response.user))
            } else {
                emit(Resource.Error(Exception("Eroare necunoscută la conectare.")))
            }
        } catch (e: Exception) {
            emit(Resource.Error(Exception(e.message ?: "Eroare de conexiune la server.")))
        }
    }

    suspend fun register(fullName: String, email: String, password: String): Flow<Resource<User>> = flow {
        emit(Resource.Loading)
        try {
            val response = RetrofitClient.instance.register(RegisterRequest(fullName, email, password))
            if (response.status == "success" && response.user != null) {
                emit(Resource.Success(response.user))
            } else {
                emit(Resource.Error(Exception("Eroare necunoscută la înregistrare.")))
            }
        } catch (e: Exception) {
            emit(Resource.Error(Exception(e.message ?: "Eroare de conexiune la server.")))
        }
    }
}