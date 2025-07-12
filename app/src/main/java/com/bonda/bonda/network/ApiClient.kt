package com.bonda.bonda.network

import com.bonda.bonda.network.service.ArticleService
import com.bonda.bonda.network.service.AuthService
import com.bonda.bonda.network.service.BookService
import com.bonda.bonda.network.service.MemberService
import com.bonda.bonda.network.service.SearchService
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

    lateinit var articleService: ArticleService
        private set
    lateinit var authService: AuthService
        private set
    lateinit var bookService: BookService
        private set
    lateinit var memberService: MemberService
        private set
    lateinit var searchService: SearchService
        private set

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

        articleService = retrofit.create(ArticleService::class.java)
        authService = retrofit.create(AuthService::class.java)
        bookService = retrofit.create(BookService::class.java)
        memberService = retrofit.create(MemberService::class.java)
        searchService = retrofit.create(SearchService::class.java)
    }
}