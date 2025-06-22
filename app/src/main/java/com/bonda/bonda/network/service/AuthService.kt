package com.bonda.bonda.network.service

import com.bonda.bonda.network.ApiResponse
import com.bonda.bonda.network.model.auth.LoginRequest
import com.bonda.bonda.network.model.auth.LoginResponse
import com.bonda.bonda.network.model.auth.ReissueRequest
import com.bonda.bonda.network.model.auth.ReissueResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    /**
     * 회원가입 + 로그인
     */
    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): ApiResponse<LoginResponse>

    /**
     * AccessToken 재발급
     */
    @POST("auth/reissue")
    suspend fun reissueAccessToken(
        @Body request: ReissueRequest
    ): ApiResponse<ReissueResponse>

}
