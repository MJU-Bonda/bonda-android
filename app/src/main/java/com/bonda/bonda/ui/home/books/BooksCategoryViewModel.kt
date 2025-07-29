package com.bonda.bonda.ui.home.books

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bonda.bonda.R

class BooksCategoryViewModel : ViewModel() {
    // private live data
    private val _categories = MutableLiveData<List<String>>()
    private val _selectedCategory = MutableLiveData<String>()
    private val _books = MutableLiveData<List<Book>>()

    // read-only properties
    val categories: LiveData<List<String>> = _categories
    val selectedCategory: LiveData<String> = _selectedCategory
    val books: LiveData<List<Book>> = _books

    // data-class declaration
    data class Book (
        val id: Int,
        val coverImage: Int,
        val category: String,
        val title: String,
        val author : String
    )

    init {
        _categories.value = listOf(
            "소설", "시집", "에세이", "비평/평론", "만화", "기술", "교육", "여행", "요리"
        )
        _selectedCategory.value = "에세이"
        _books.value = emptyList()
    }
}