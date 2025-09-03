package com.bonda.bonda.ui.home.books

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bonda.bonda.model.BookCategory
import com.bonda.bonda.model.BookTheme
import com.bonda.bonda.network.ApiClient
import com.bonda.bonda.model.TAG
import kotlinx.coroutines.launch

class BooksViewModel : ViewModel() {
    /**
     * network service 선언
     */
    private val bookService = ApiClient.bookService

    /**
     * 현재 진행 중인 네트워크 요청의 수를 추적하는 카운터
     */
    private var activeNetworkCalls = 0

    /**
     * view model 데이터 선언
     */
    private val _isLoading = MutableLiveData(true)
    private val _isError = MutableLiveData(false)
    private val _selectedNewArrivedBooksCategory = MutableLiveData(BookCategory.ALL.code)
    private val _selectedMostLovedBooksCategory = MutableLiveData(BookTheme.ALL.code)
    private val _recentArrivalBooks = MutableLiveData<List<Book>>()
    private val _mostLovedBooks = MutableLiveData<List<Book>>()

    /**
     * 관찰용 live data
     */
    val isLoading: LiveData<Boolean> = _isLoading
    val isError: LiveData<Boolean> = _isError
    val recentArrivalBooks: LiveData<List<Book>> = _recentArrivalBooks
    val mostLovedBooks: LiveData<List<Book>> = _mostLovedBooks

    /**
     * 방금 도착한 새로운 책 조회
     */
    fun setSelectedNewArrivedBooksCategory(category: String) {
        if (activeNetworkCalls == 0) {
            _isError.value = false
        }
        _isLoading.value = true
        activeNetworkCalls++

        viewModelScope.launch {
            try {
                val res = bookService.getBooksByCategory(size = 3, category = category)
                    .unwrapOrThrow()

                _recentArrivalBooks.value = res.bookList.map {
                    Book(
                        id = it.id,
                        coverImage = it.imageUrl,
                        category = it.category,
                        title = it.title,
                        author = it.author
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "BooksViewModel.kt::setSelectedNewArrivedBooksCategory()", e)
                _isError.value = true
            } finally {
                activeNetworkCalls--
                if (activeNetworkCalls == 0) {
                    _isLoading.value = false
                }
            }
        }
    }

    /**
     * 가장 많이 사랑 받은 책 조회
     */
    fun setSelectedMostLovedBooksCategory(category: String) {
        if (activeNetworkCalls == 0) {
            _isError.value = false
        }
        _isLoading.value = true
        activeNetworkCalls++

        viewModelScope.launch {
            try {
                val res = bookService.getMostLovedBooks(subject = category)
                    .unwrapOrThrow()

                _mostLovedBooks.value = res.bookList.map {
                    Book(
                        id = it.id,
                        coverImage = it.imageUrl,
                        category = it.category,
                        title = it.title,
                        author = it.author
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "BooksViewModel.kt::setSelectedMostLovedBooksCategory()", e)
                _isError.value = true
            } finally {
                activeNetworkCalls--
                if (activeNetworkCalls == 0) {
                    _isLoading.value = false
                }
            }
        }
    }

    /**
     * 데이터 초기화 및 재시도용 함수
     */
    fun fetchInitialData() {
        setSelectedMostLovedBooksCategory(BookCategory.ALL.code)
        setSelectedNewArrivedBooksCategory(BookCategory.ALL.code)
    }

    /**
     * 데이터 초기화
     */
    init {
        fetchInitialData()
    }

}