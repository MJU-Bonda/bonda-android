package com.bonda.bonda.network.service

import com.bonda.bonda.network.ApiResponse
import com.bonda.bonda.network.model.search.DeleteSearchAllHistoryResponse
import com.bonda.bonda.network.model.search.DeleteSearchHistoryResponse
import com.bonda.bonda.network.model.search.GetRecommendedKeywordResponse
import com.bonda.bonda.network.model.search.GetSearchHistoryResponse
import com.bonda.bonda.network.model.search.SearchArticlesResponse
import com.bonda.bonda.network.model.search.SearchBooksResponse
import com.bonda.bonda.network.model.search.ToggleAutoSaveResponse
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.Query

interface SearchService {
    /**
     * 검색어 저장 여부를 설정합니다
     */
    @PATCH("/search-term/auto-save")
    suspend fun toggleAutoSave(): ApiResponse<ToggleAutoSaveResponse>

    /**
     * 추천 키워드 목록을 조회합니다
     */
    @GET("/search-term/recommend")
    suspend fun getRecommendedKeyword(): ApiResponse<GetRecommendedKeywordResponse>

    /**
     * 회원의 최근 검색어 목록을 조회합니다
     */
    @GET("/search-term/recent")
    suspend fun getSearchHistory(): ApiResponse<GetSearchHistoryResponse>

    /**
     * 회원의 최근 검색어를 삭제합니다
     */
    @DELETE("/search-term/recent")
    suspend fun deleteSearchHistory(
        @Query("keyword") keyword: String
    ): ApiResponse<DeleteSearchHistoryResponse>

    /**
     * 회원의 최근 검색어를 모두 삭제합니다
     */
    @DELETE("/search-term/recent/all")
    suspend fun deleteAllSearchHistory(): ApiResponse<DeleteSearchAllHistoryResponse>

    /**
     * 도서를 검색합니다
     */
    @GET("/books/search")
    suspend fun searchBooks(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 24,
        @Query("orderBy") orderBy: String = "newest",
        @Query("word") word: String
    ): ApiResponse<SearchBooksResponse>

    /**
     * 아티클을 검색합니다
     */
    @GET("/articles/search")
    suspend fun searchArticles(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 24,
        @Query("orderBy") orderBy: String = "newest",
        @Query("word") word: String
    ): ApiResponse<SearchArticlesResponse>

}