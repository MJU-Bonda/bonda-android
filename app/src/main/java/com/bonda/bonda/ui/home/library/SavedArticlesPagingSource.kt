package com.bonda.bonda.ui.home.library

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.bonda.bonda.network.model.article.SavedArticlesResponse
import com.bonda.bonda.network.service.ArticleService

class SavedArticlesPagingSource(
    private val service: ArticleService
) : PagingSource<Int, SavedArticlesResponse.Article>() {

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, SavedArticlesResponse.Article> {
        val page = params.key ?: 0
        return try {
            val resp = service.getSavedArticles(page = page, size = params.loadSize)
            val data = resp.data.articleList
            LoadResult.Page(
                data = data,
                prevKey = if (page == 0) null else page - 1,
                nextKey = if (resp.data.hasNextPage) page + 1 else null
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, SavedArticlesResponse.Article>): Int? =
        state.anchorPosition?.let { pos ->
            state.closestPageToPosition(pos)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(pos)?.nextKey?.minus(1)
        }

}
