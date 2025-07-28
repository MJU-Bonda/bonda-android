package com.bonda.bonda.ui.home.library

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.bonda.bonda.network.ApiClient.bookService
import com.bonda.bonda.network.model.book.SavedBooksResponse
import kotlinx.coroutines.flow.Flow

class LibraryViewModel : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    private val _savedBookCount = MutableLiveData<Int>()
    private val _articleCount = MutableLiveData<Int>()

    val isLoading: LiveData<Boolean> = _isLoading
    val savedBookCount: LiveData<Int> = _savedBookCount
    val articleCount: LiveData<Int> = _articleCount

    init {
        
    }

    val savedBooksFlow: Flow<PagingData<SavedBooksResponse.Book>> =
        Pager(
            config = PagingConfig(pageSize = 24, prefetchDistance = 2, enablePlaceholders = false),
            pagingSourceFactory = { SavedBooksPagingSource(bookService) }
        ).flow.cachedIn(viewModelScope)

}