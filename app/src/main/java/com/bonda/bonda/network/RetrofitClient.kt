package com.bonda.bonda.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.http.Body
import retrofit2.http.POST

private const val BASE_URL = "http://44.204.30.99:8080"

private val retrofit = Retrofit.Builder()
    .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
    .baseUrl(BASE_URL)
    .build()

/**
 * ??what
 */
@Serializable
data class ApiResponse<T>(
    val success: Boolean,
    val data: T
)

fun <T> ApiResponse<T>.unwrap(): T {
    if (!success) {
        throw ApiException("API 호출 실패: success=false")
    }
    return data
}

class ApiException(message: String) : RuntimeException(message)

interface ApiService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): ApiResponse<LoginResponse>

    @POST("auth/reissue")
    suspend fun reissueAccessToken(@Body request: ReissueRequest): ApiResponse<ReissueResponse>
}

object RetrofitClient {
    val retrofitService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}

@Serializable
data class LoginRequest(
    val idToken: String
)
@Serializable
data class LoginResponse(
    val accessToken: String,
    val refreshToken: String,
    val isNewUser: Boolean
)

@Serializable
data class ReissueRequest(
    val refreshToken: String
)
@Serializable
data class ReissueResponse(
    val accessToken: String
)
