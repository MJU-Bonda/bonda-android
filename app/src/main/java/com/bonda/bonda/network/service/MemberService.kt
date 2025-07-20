package com.bonda.bonda.network.service

import com.bonda.bonda.network.ApiResponse
import com.bonda.bonda.network.model.member.GetBadgeDetailResponse
import com.bonda.bonda.network.model.member.GetCollectedBadgesResponse
import com.bonda.bonda.network.model.member.GetMyActivityResponse
import com.bonda.bonda.network.model.member.GetProfileResponse
import com.bonda.bonda.network.model.member.UpdateProfileResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface MemberService {
    /**
     * 회원의 닉네임 또는 프로필 이미지 변경
     */
    @Multipart
    @PUT("members/update/nickname-image")
    suspend fun updateProfile(
        @Part("member") member: RequestBody,
        @Part("nickname") nickname: RequestBody,
        @Part profileImage: MultipartBody.Part?
    ): ApiResponse<UpdateProfileResponse>

    /**
     * 마이페이지 내 회원 정보 조회
     */
    @GET("members/my-page")
    suspend fun getProfile(): ApiResponse<GetProfileResponse>

    /**
     * 내 활동 정보 조회
     */
    @GET("members/my-activity")
    suspend fun getMyActivity(): ApiResponse<GetMyActivityResponse>

    /**
     * 수집한 뱃지 목록
     */
    @GET("badges/me")
    suspend fun getCollectedBadges(): ApiResponse<GetCollectedBadgesResponse>

    /**
     * 뱃지 상세 조회
     */
    @GET("badges/{badgeId}")
    suspend fun getBadgeDetail(
        @Path("badgeId") badgeId: Int
    ): ApiResponse<GetBadgeDetailResponse>
}
