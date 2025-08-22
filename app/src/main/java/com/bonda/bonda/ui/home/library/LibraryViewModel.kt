package com.bonda.bonda.ui.home.library

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.bonda.bonda.model.SortOrder
import com.bonda.bonda.network.ApiClient
import com.bonda.bonda.network.model.article.SavedArticlesResponse
import com.bonda.bonda.network.model.book.SavedBooksResponse
import com.bonda.bonda.util.TAG
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class LibraryViewModel : ViewModel() {
    /**
     * 네트워크 서비스 인스턴스 생성
     */
    private val bookService = ApiClient.bookService
    private val articleService = ApiClient.articleService

    /**
     * 관찰용 live-data 선언
     */
    private val _savedBookCount = MutableLiveData<Int>()
    private val _savedArticleCount = MutableLiveData<Int>()
    val savedBookCount: LiveData<Int> = _savedBookCount
    val savedArticleCount: LiveData<Int> = _savedArticleCount

    /**
     * 정렬 기준 관리
     */
    private val _bookSortOrder = MutableStateFlow(SortOrder.RECENTLY_SAVED.code)
    private val _articleSortOrder = MutableStateFlow(SortOrder.RECENTLY_SAVED.code)

    val bookSortOrder = _bookSortOrder.asStateFlow()
    val articleSortOrder = _articleSortOrder.asStateFlow()

    fun toggleBookSortOrder() {
        if (_bookSortOrder.value == SortOrder.RECENTLY_SAVED.code) {
            _bookSortOrder.value = SortOrder.TITLE.code
        } else {
            _bookSortOrder.value = SortOrder.RECENTLY_SAVED.code
        }
    }

    fun toggleArticleSortOrder() {
        if (_articleSortOrder.value == SortOrder.RECENTLY_SAVED.code) {
            _articleSortOrder.value = SortOrder.TITLE.code
        } else {
            _articleSortOrder.value = SortOrder.RECENTLY_SAVED.code
        }
    }

    /**
     * 저장한 도서와 아티클 갯수를 불러옵니다.
     */
    init {
        viewModelScope.launch {
            try {
                _savedBookCount.value = bookService.getSavedBooks().unwrapOrThrow().total
                _savedArticleCount.value = articleService.getSavedArticles().unwrapOrThrow().total
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
            }
        }
    }

    /**
     * 저장한 도서를 페이지네이션합니다.
     */
    val savedBooksFlow: Flow<PagingData<SavedBooksResponse.Book>> =
        bookSortOrder.flatMapLatest { orderBy ->
            Pager(
                config = PagingConfig(
                    pageSize = 24,
                    prefetchDistance = 2,
                    enablePlaceholders = false
                ),
                pagingSourceFactory = { SavedBooksPagingSource(bookService, orderBy) }
            ).flow
        }.cachedIn(viewModelScope)


    /**
     * 저장한 아티클을 페이지네이션합니다.
     */
    val savedArticlesFlow: Flow<PagingData<SavedArticlesResponse.Article>> =
        articleSortOrder.flatMapLatest { orderBy ->

            Pager(
                config = PagingConfig(
                    pageSize = 24,
                    prefetchDistance = 2,
                    enablePlaceholders = false
                ),
                pagingSourceFactory = { SavedArticlesPagingSource(articleService, orderBy) }
            ).flow
        }.cachedIn(viewModelScope)


}
