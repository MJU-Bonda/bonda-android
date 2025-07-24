package com.bonda.bonda.network.service

import com.bonda.bonda.network.ApiResponse
import com.bonda.bonda.network.model.auth.LoginRequest
import com.bonda.bonda.network.model.auth.LoginResponse
import com.bonda.bonda.network.model.auth.LogoutRequest
import com.bonda.bonda.network.model.auth.LogoutResponse
import com.bonda.bonda.network.model.auth.ReissueRequest
import com.bonda.bonda.network.model.auth.ReissueResponse
import com.bonda.bonda.network.model.auth.WithdrawalResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
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

    /**
     * 로그아웃
     */
    @POST("auth/logout")
    suspend fun logout(
        @Body request: LogoutRequest
    ): ApiResponse<LogoutResponse>

    /**
     * 회원탈퇴
     */
    @DELETE("auth/exit")
    suspend fun withdrawal(): ApiResponse<WithdrawalResponse>
}
