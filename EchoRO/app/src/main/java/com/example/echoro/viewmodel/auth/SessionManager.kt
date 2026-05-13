package com.example.echoro.viewmodel.auth

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_session")

class SessionManager(private val context: Context) {
    companion object {
        val USER_ID = intPreferencesKey("user_id")
        val IS_GUEST = booleanPreferencesKey("is_guest")
    }

    val userIdFlow: Flow<Int> = context.dataStore.data.map { it[USER_ID] ?: 0 }
    val isGuestFlow: Flow<Boolean> = context.dataStore.data.map { it[IS_GUEST] ?: true }

    suspend fun saveSession(userId: Int, isGuest: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[USER_ID] = userId
            prefs[IS_GUEST] = isGuest
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit { it.clear() }
    }
}