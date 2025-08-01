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
import com.bonda.bonda.network.ApiClient
import com.bonda.bonda.network.model.article.SavedArticlesResponse
import com.bonda.bonda.network.model.book.SavedBooksResponse
import com.bonda.bonda.util.TAG
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class LibraryViewModel : ViewModel() {

    private val bookService = ApiClient.bookService
    private val articleService = ApiClient.articleService

    private val _isLoading = MutableLiveData<Boolean>()
    private val _savedBookCount = MutableLiveData<Int>()
    private val _savedArticleCount = MutableLiveData<Int>()

    val isLoading: LiveData<Boolean> = _isLoading
    val savedBookCount: LiveData<Int> = _savedBookCount
    val savedArticleCount: LiveData<Int> = _savedArticleCount

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

    val savedBooksFlow: Flow<PagingData<SavedBooksResponse.Book>> =
        Pager(
            config = PagingConfig(pageSize = 24, prefetchDistance = 2, enablePlaceholders = false),
            pagingSourceFactory = { SavedBooksPagingSource(bookService) }
        ).flow.cachedIn(viewModelScope)

    val savedArticlesFlow: Flow<PagingData<SavedArticlesResponse.Article>> =
        Pager(
            config = PagingConfig(pageSize = 24, prefetchDistance = 2, enablePlaceholders = false),
            pagingSourceFactory = { SavedArticlesPagingSource(articleService) }
        ).flow.cachedIn(viewModelScope)

}