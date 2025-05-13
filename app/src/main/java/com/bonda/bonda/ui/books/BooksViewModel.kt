package com.bonda.bonda.ui.books

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class BooksViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "도서 화면 입니다"
    }
    val text: LiveData<String> = _text
}