package com.aaspaas.business.core.network

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.tokenDataStore by preferencesDataStore("business_auth_prefs")

@Singleton
class DataStoreTokenProvider @Inject constructor(
    @ApplicationContext private val context: Context
) : TokenProvider {
    private val tokenKey = stringPreferencesKey("jwt_token")
    override suspend fun getToken(): String? = context.tokenDataStore.data.map { it[tokenKey] }.firstOrNull()
    override suspend fun saveToken(token: String) { context.tokenDataStore.edit { it[tokenKey] = token } }
    override suspend fun clearToken() { context.tokenDataStore.edit { it.remove(tokenKey) } }
}
