package com.aaspaas.customer

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.sessionDataStore by preferencesDataStore("session_prefs")

@Singleton
class SessionManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val keyLoggedIn = booleanPreferencesKey("is_logged_in")
    private val keyOnboarding = booleanPreferencesKey("onboarding_done")

    val isLoggedIn: Flow<Boolean> = context.sessionDataStore.data.map { it[keyLoggedIn] ?: false }
    val onboardingDone: Flow<Boolean> = context.sessionDataStore.data.map { it[keyOnboarding] ?: false }

    suspend fun setLoggedIn(value: Boolean) {
        context.sessionDataStore.edit { it[keyLoggedIn] = value }
    }

    suspend fun setOnboardingDone() {
        context.sessionDataStore.edit { it[keyOnboarding] = true }
    }

    suspend fun clearSession() {
        context.sessionDataStore.edit { it.clear() }
    }
}
