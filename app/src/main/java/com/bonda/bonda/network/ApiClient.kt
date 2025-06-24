package com.bonda.bonda.network

import com.bonda.bonda.network.service.AuthService
import com.bonda.bonda.network.service.MemberService
import com.bonda.bonda.util.AccessTokenProvider
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.json.Json
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

object ApiClient {

    private lateinit var retrofit: Retrofit

    lateinit var authService: AuthService
        private set
    lateinit var memberService: MemberService

    fun init(accessTokenProvider: AccessTokenProvider) {
        val json = Json {
            ignoreUnknownKeys = true
            explicitNulls = false
        }

        val authInterceptor = Interceptor { chain ->
            val reqBuilder = chain.request().newBuilder()
            accessTokenProvider.getAccessToken()?.let { token ->
                reqBuilder.header(HEADER_AUTHORIZATION, "Bearer $token")
            }
            chain.proceed(reqBuilder.build())
        }

        val client = OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()

        authService = retrofit.create(AuthService::class.java)
        memberService = retrofit.create(MemberService::class.java)
    }
}