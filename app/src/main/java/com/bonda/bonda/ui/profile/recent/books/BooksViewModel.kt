package com.bonda.bonda.ui.profile.recent.books

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bonda.bonda.network.ApiClient
import com.bonda.bonda.util.TAG
import kotlinx.coroutines.launch

class BooksViewModel : ViewModel() {

    companion object {
        private const val PAGE_SIZE = 24
    }

    private val bookService = ApiClient.bookService
    private var page = 0

    private val _isLoading = MutableLiveData(false)
    private val _error = MutableLiveData(false)
    private val _books = MutableLiveData<List<Book>>()
    private val _hasNextPage = MutableLiveData(false)

    val isLoading: LiveData<Boolean> = _isLoading
    val error: LiveData<Boolean> = _error
    val books:LiveData<List<Book>> = _books
    val hasNextPage:LiveData<Boolean> = _hasNextPage

    fun getBooks() {
        viewModelScope.launch {
            _isLoading.value = true

            try {
                val res = bookService.getRecentViewedBooks().unwrapOrThrow()
                Log.d(TAG, res)
            } catch (e:Exception) {
                Log.e(TAG, e.message.toString())
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getNextPage() {
        if(_hasNextPage.value != true) return

        page++
        getBooks()
    }

}
