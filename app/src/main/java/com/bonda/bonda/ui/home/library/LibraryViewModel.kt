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
import com.bonda.bonda.model.AppEvents
import com.bonda.bonda.model.SortOrder
import com.bonda.bonda.network.ApiClient
import com.bonda.bonda.network.model.article.SavedArticlesResponse
import com.bonda.bonda.network.model.book.SavedBooksResponse
import com.bonda.bonda.model.TAG
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class LibraryViewModel : ViewModel() {
    /**
     * 네트워크 서비스 인스턴스 생성
     */
    private val bookService = ApiClient.bookService
    private val articleService = ApiClient.articleService

    /**
     * Paging 데이터 리프레시를 위한 트리거
     */
    private val refreshTrigger = MutableStateFlow(0)

    /**
     * 관찰용 live-data 선언
     */
    private val _isLoading = MutableLiveData(true)
    private val _isError = MutableLiveData(false)
    private val _savedBookCount = MutableLiveData<Int>()
    private val _savedArticleCount = MutableLiveData<Int>()

    val isLoading: LiveData<Boolean> = _isLoading
    val isError: LiveData<Boolean> = _isError
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
     * 저장한 도서와 아티클 갯수를 불러옵니다
     * reloadData()를 이용해서 초기 데이터를 불러오고, AppEvents를 구독해서 이벤트를 받으면 reloadData()를 실행합니다.
     */
    init {
        reloadData()

        viewModelScope.launch {
            AppEvents.libraryUpdated.collect {
                reloadData()
            }
        }
    }

    /**
     * 서재의 모든 데이터를 다시 불러옵니다
     */
    fun reloadData() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _isError.value = false

                _savedBookCount.value = bookService.getSavedBooks().unwrapOrThrow().total
                _savedArticleCount.value = articleService.getSavedArticles().unwrapOrThrow().total
            } catch (e: Exception) {
                Log.e(TAG, "LibraryViewModel.kt::reloadData()", e)
                _isError.value = true
            } finally {
                _isLoading.value = false
            }

            refreshTrigger.value++
        }
    }

    /**
     * 저장한 도서를 페이지네이션합니다.
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    val savedBooksFlow: Flow<PagingData<SavedBooksResponse.Book>> =
        combine(bookSortOrder, refreshTrigger) { orderBy, _ ->
            orderBy
        }.flatMapLatest { orderBy ->
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
    @OptIn(ExperimentalCoroutinesApi::class)
    val savedArticlesFlow: Flow<PagingData<SavedArticlesResponse.Article>> =
        combine(articleSortOrder, refreshTrigger) { orderBy, _ ->
            orderBy
        }.flatMapLatest { orderBy ->
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
