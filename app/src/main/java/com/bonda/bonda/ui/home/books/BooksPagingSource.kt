package com.bonda.bonda.ui.home.books

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.bonda.bonda.network.model.book.BooksByCategoryResponse
import com.bonda.bonda.network.service.BookService

class BooksPagingSource(
    private val service: BookService,
    private val category: String
) : PagingSource<Int, BooksByCategoryResponse.Book>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, BooksByCategoryResponse.Book> {
        val page = params.key?: 0
        return try {
            val resp = service.getBooksByCategory(page = page, size = params.loadSize, category = category)
            val data = resp.data.bookList
            LoadResult.Page(
                data = data,
                prevKey = if (page == 0) null else page - 1,
                nextKey = if (resp.data.hasNextPage) page + 1 else null
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, BooksByCategoryResponse.Book>): Int? =
        state.anchorPosition?.let { pos ->
            state.closestPageToPosition(pos)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(pos)?.nextKey?.minus(1)
        }

}
