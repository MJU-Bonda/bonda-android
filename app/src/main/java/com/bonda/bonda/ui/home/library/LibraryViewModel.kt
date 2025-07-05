package com.bonda.bonda.ui.home.library

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LibraryViewModel : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    private val _bookCount = MutableLiveData<Int>()
    private val _books = MutableLiveData<List<Book>>()
    private val _articleCount = MutableLiveData<Int>()
    private val _articles = MutableLiveData<List<Article>>()

    val isLoading: LiveData<Boolean> = _isLoading
    val bookCount: LiveData<Int> = _bookCount
    val books: LiveData<List<Book>> = _books
    val articleCount: LiveData<Int> = _articleCount
    val articles: LiveData<List<Article>> = _articles

    data class Book(
        val bookId: Int,
        val imgSrc: String,
    )

    data class Article(
        val articleId: Int,
        val imgSrc: String,
        val category: String
    )

    init {
        
    }
}