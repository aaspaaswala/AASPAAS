package com.aaspaas.business

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.sessionDataStore by preferencesDataStore("business_session_prefs")

@Singleton
class BusinessSessionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val keyLoggedIn = booleanPreferencesKey("is_logged_in")

    val isLoggedIn: Flow<Boolean> = context.sessionDataStore.data.map { it[keyLoggedIn] ?: false }

    suspend fun setLoggedIn(value: Boolean) { context.sessionDataStore.edit { it[keyLoggedIn] = value } }
    suspend fun clearSession() { context.sessionDataStore.edit { it.clear() } }
}
