package com.aaspaas.customer

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
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
    private val keyUserName = stringPreferencesKey("user_name")
    private val keyUserEmail = stringPreferencesKey("user_email")
    private val keyUserDob = stringPreferencesKey("user_dob")

    val isLoggedIn: Flow<Boolean> = context.sessionDataStore.data.map { it[keyLoggedIn] ?: false }
    val onboardingDone: Flow<Boolean> = context.sessionDataStore.data.map { it[keyOnboarding] ?: false }
    val userName: Flow<String?> = context.sessionDataStore.data.map { it[keyUserName] }
    val userEmail: Flow<String?> = context.sessionDataStore.data.map { it[keyUserEmail] }
    val userDob: Flow<String?> = context.sessionDataStore.data.map { it[keyUserDob] }

    suspend fun setLoggedIn(value: Boolean) {
        context.sessionDataStore.edit { it[keyLoggedIn] = value }
    }

    suspend fun setOnboardingDone() {
        context.sessionDataStore.edit { it[keyOnboarding] = true }
    }

    suspend fun saveProfile(name: String?, email: String?, dob: String?) {
        context.sessionDataStore.edit {
            if (name != null) it[keyUserName] = name else it.remove(keyUserName)
            if (email != null) it[keyUserEmail] = email else it.remove(keyUserEmail)
            if (dob != null) it[keyUserDob] = dob else it.remove(keyUserDob)
        }
    }

    suspend fun clearSession() {
        context.sessionDataStore.edit { it.clear() }
    }
}
