package com.bonda.bonda.network.service

import com.bonda.bonda.network.ApiResponse
import com.bonda.bonda.network.model.book.BookDetailResponse
import com.bonda.bonda.network.model.book.BooksByCategoryResponse
import com.bonda.bonda.network.model.book.CreateBookResponse
import com.bonda.bonda.network.model.book.NewBookRequest
import com.bonda.bonda.network.model.book.RecentViewedBooksResponse
import com.bonda.bonda.network.model.book.SaveBookResponse
import com.bonda.bonda.network.model.book.SavedBooksResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface BookService {
    @POST("books/save/{bookId}")
    suspend fun saveBook(
        @Path("bookId") bookId: Long
    ): ApiResponse<SaveBookResponse>

    @POST("books/new")
    suspend fun createBook(
        @Body request: NewBookRequest
    ): ApiResponse<CreateBookResponse>

    @GET("books")
    suspend fun getBooksByCategory(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 24,
        @Query("orderBy") orderBy: String = "popularity",
        @Query("category") category: String = "ALL"
    ): ApiResponse<BooksByCategoryResponse>

    @GET("books/{bookId}")
    suspend fun getBookDetail(
        @Path("bookId") bookId: Long
    ): ApiResponse<BookDetailResponse>

    @GET("books/my-save")
    suspend fun getSavedBooks(
        @Query("page") page:Int = 0,
        @Query("size") size: Int = 24,
        @Query("orderBy") orderBy: String = "recentlySaved"
    ): ApiResponse<SavedBooksResponse>

    @GET("books/my-recent-views")
    suspend fun getRecentViewedBooks (
        @Query("page") page:Int = 0,
        @Query("size") size: Int = 24
    ):ApiResponse<RecentViewedBooksResponse>
}