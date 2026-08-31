package com.aaspaas.business.core.network

import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject
import javax.inject.Provider

interface TokenProvider {
    suspend fun getToken(): String?
    suspend fun getRefreshToken(): String?
    suspend fun saveToken(token: String)
    suspend fun saveTokens(accessToken: String, refreshToken: String)
    suspend fun clearToken()
}

class AuthInterceptor @Inject constructor(
    private val tokenProvider: Provider<TokenProvider>
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = runBlocking { tokenProvider.get().getToken() }
        val request = if (token != null) {
            chain.request().newBuilder().addHeader("Authorization", "Bearer $token").build()
        } else chain.request()
        return chain.proceed(request)
    }
}

sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error(val code: Int, val message: String) : ApiResult<Nothing>()
    data class Exception(val throwable: Throwable) : ApiResult<Nothing>()
}

suspend fun <T> safeApiCall(call: suspend () -> T): ApiResult<T> = try {
    ApiResult.Success(call())
} catch (e: retrofit2.HttpException) {
    ApiResult.Error(e.code(), e.response()?.errorBody()?.string() ?: e.message())
} catch (e: java.io.IOException) {
    ApiResult.Exception(e)
} catch (e: Throwable) {
    ApiResult.Exception(e)
}

fun <T> ApiResult<T>.toResult(): Result<T> = when (this) {
    is ApiResult.Success -> Result.success(data)
    is ApiResult.Error -> Result.failure(RuntimeException("API Error $code: $message"))
    is ApiResult.Exception -> Result.failure(throwable)
}
