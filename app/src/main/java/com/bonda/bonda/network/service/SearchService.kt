package com.bonda.bonda.network.service

import com.bonda.bonda.network.ApiResponse
import com.bonda.bonda.network.model.search.GetSearchHistoryRequest
import com.bonda.bonda.network.model.search.GetSearchHistoryResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface SearchService {
    /**
     * 회원의 최근 검색어 목록을 조회합니다.
     */
    @GET("/search-term/recent")
    suspend fun getSearchHistory(
        @Query("member") member: String
    ): ApiResponse<GetSearchHistoryResponse>

}