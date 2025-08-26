package com.bonda.bonda.ui.home.books

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asFlow
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.bonda.bonda.model.BookCategory
import com.bonda.bonda.model.SortOrder
import com.bonda.bonda.network.ApiClient
import com.bonda.bonda.network.model.book.BooksByCategoryResponse
import com.bonda.bonda.model.TAG
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch

class BooksCategoryViewModel : ViewModel() {
    /**
     * network service 선언
     */
    private val bookService = ApiClient.bookService

    /**
     * view model 데이터 선언
     */
    private val _categories = MutableLiveData(BookCategory.entries.map { it.code })
    private val _selectedCategory = MutableLiveData(BookCategory.ALL.code)
    private val _totalBookCount = MutableLiveData(0)
    private val _orderBy = MutableLiveData(SortOrder.POPULARITY.code)

    /**
     * 관찰용 live data
     */
    val categories: LiveData<List<String>> = _categories
    val selectedCategory: LiveData<String> = _selectedCategory
    val totalBookCount: LiveData<Int> = _totalBookCount
    val orderBy: LiveData<String> = _orderBy

    /**
     * 도서 목록 페이지네이션
     */
    @OptIn(ExperimentalCoroutinesApi::class)
    val booksFlow: Flow<PagingData<BooksByCategoryResponse.Book>> =
        combine(
            selectedCategory.asFlow(),
            orderBy.asFlow()
        ) { category, orderBy ->
            Pair(category, orderBy)
        }.flatMapLatest { (category, orderBy) ->
            Pager(
                config = PagingConfig(
                    pageSize = 24,
                    prefetchDistance = 2,
                    enablePlaceholders = false
                ),
                pagingSourceFactory = { BooksPagingSource(bookService, category, orderBy) }
            ).flow
        }.cachedIn(viewModelScope)

    /**
     * 카테고리 변경 및 총 도서 갯수 로드
     */
    fun setSelectedCategory(category: String) {
        _selectedCategory.value = category

        viewModelScope.launch {
            try {
                val resp = bookService.getBooksByCategory(
                    size = 1,
                    category = category
                ).unwrapOrThrow()
                _totalBookCount.value = resp.total
            } catch (e: Exception) {
                Log.e(TAG, e.message.toString())
            }
        }
    }

    /**
     * 정렬 기준을 토글합니다
     */
    fun toggleSortOrder() {
        if (_orderBy.value == SortOrder.POPULARITY.code)
            _orderBy.value = SortOrder.RECENT.code
        else
            _orderBy.value = SortOrder.POPULARITY.code

    }

}
