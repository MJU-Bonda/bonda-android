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
     * view model 데이터 선언
     */
    private val _selectedNewArrivedBooksCategory = MutableLiveData(BookCategory.ALL.code)
    private val _selectedMostLovedBooksCategory = MutableLiveData(BookTheme.ALL.code)
    private val _recentArrivalBooks = MutableLiveData<List<Book>>()
    private val _mostLovedBooks = MutableLiveData<List<Book>>()

    /**
     * 관찰용 live data
     */
    val recentArrivalBooks: LiveData<List<Book>> = _recentArrivalBooks
    val mostLovedBooks: LiveData<List<Book>> = _mostLovedBooks

    /**
     * 방금 도착한 새로운 책 조회
     */
    fun setSelectedNewArrivedBooksCategory(category: String) {
        viewModelScope.launch {
            try {
                val res = bookService.getBooksByCategory(
                    size = 3,
                    category = category
                ).unwrapOrThrow()

                Log.d(TAG, res.toString())

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
                Log.e(TAG, e.message.toString())
            }
        }
    }

    /**
     * 가장 많이 사랑 받은 책 조회
     */
    fun setSelectedMostLovedBooksCategory(category: String) {
        viewModelScope.launch {
            try {
                val res = bookService.getMostLovedBooks(
                    subject = category
                ).unwrapOrThrow()

                Log.d(TAG, res.toString())

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
                Log.e(TAG, e.message.toString())
            }
        }
    }

    /**
     * 데이터 초기화
     */
    init {
        setSelectedMostLovedBooksCategory(BookCategory.ALL.code)
        setSelectedNewArrivedBooksCategory(BookCategory.ALL.code)
    }

}