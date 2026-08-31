package com.aaspaas.customer.core.network

sealed class ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>()
    data class Error(val code: Int, val message: String) : ApiResult<Nothing>()
    data class Exception(val throwable: Throwable) : ApiResult<Nothing>()
}

suspend fun <T> safeApiCall(call: suspend () -> T): ApiResult<T> = try {
    ApiResult.Success(call())
} catch (e: retrofit2.HttpException) {
    val body = e.response()?.errorBody()?.string() ?: e.message()
    ApiResult.Error(e.code(), body)
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
