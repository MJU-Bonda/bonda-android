package com.bonda.bonda.network.service

import com.bonda.bonda.network.ApiResponse
import com.bonda.bonda.network.model.member.GetProfileRequest
import com.bonda.bonda.network.model.member.GetProfileResponse
import com.bonda.bonda.network.model.member.UpdateProfileRequest
import com.bonda.bonda.network.model.member.UpdateProfileResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PUT

interface MemberService {
    /**
     * 회원의 닉네임 또는 프로필 이미지 변경
     */
    @PUT("members/update/nickname-image")
    suspend fun updateProfile(
        @Body request: UpdateProfileRequest
    ): ApiResponse<UpdateProfileResponse>

    /**
     * 마이페이지 내 회원 정보 조회
     */
    @GET("members/my-page")
    suspend fun getProfile(
        @Body request: GetProfileRequest
    ): ApiResponse<GetProfileResponse>

}
