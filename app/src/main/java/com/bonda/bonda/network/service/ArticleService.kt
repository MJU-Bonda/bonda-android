package com.bonda.bonda.network.service

import com.bonda.bonda.network.ApiResponse
import com.bonda.bonda.network.model.article.DeleteSavedArticleResponse
import com.bonda.bonda.network.model.article.GetArticleDetailResponse
import com.bonda.bonda.network.model.article.GetArticlesResponse
import com.bonda.bonda.network.model.article.GetRecentViewedArticlesResponse
import com.bonda.bonda.network.model.article.GetSavedArticlesResponse
import com.bonda.bonda.network.model.article.SaveArticleResponse
import com.bonda.bonda.network.model.article.SearchArticlesResponse
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ArticleService {
    /**
     * 아티클 저장
     */
    @POST("articles/save/{articleId}")
    suspend fun saveArticle(
        @Path("articleId") articleId: Long
    ): ApiResponse<SaveArticleResponse>

    /**
     * 아티클 저장 삭제
     */
    @DELETE("articles/save/{articleId}")
    suspend fun deleteSavedArticle(
        @Path("articleId") articleId: Long
    ): ApiResponse<DeleteSavedArticleResponse>

    /**
     * 아티클 홈 화면 (카테고리별 조회)
     */
    @GET("articles")
    suspend fun getArticles(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10,
        @Query("articleCategory") articleCategory: String
    ): ApiResponse<GetArticlesResponse>

    /**
     * 아티클 상세 조회
     */
    @GET("articles/{articleId}")
    suspend fun getArticleDetail(
        @Path("articleId") articleId: Long
    ): ApiResponse<GetArticleDetailResponse>

    /**
     * 아티클 검색
     */
    @GET("articles/search")
    suspend fun searchArticles(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 24,
        @Query("orderBy") orderBy: String = "newest",
        @Query("word") word: String
    ): ApiResponse<SearchArticlesResponse>

    /**
     * 저장한 아티클 검색
     */
    @GET("articles/my-save")
    suspend fun getSavedArticles(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 24,
        @Query("orderBy") orderBy: String = "recentlySaved"
    ): ApiResponse<GetSavedArticlesResponse>

    /**
     * 최근 조회한 아티클 목록 조회
     */
    @GET("articles/my-recent-views")
    suspend fun getRecentViewedArticles(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 24
    ): ApiResponse<GetRecentViewedArticlesResponse>
}