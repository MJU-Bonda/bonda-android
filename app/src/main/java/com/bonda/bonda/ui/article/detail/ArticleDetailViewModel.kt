package com.bonda.bonda.ui.article.detail

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ArticleDetailViewModel : ViewModel() {
    private val _title = MutableLiveData<String>("한 가지 사물, 수백 가지 이야기")
    private val _subTitle = MutableLiveData<String>("한 가지 사물에 숨겨진 다채로운 순간들")
    private val _bodys = MutableLiveData<MutableList<String>>()
    private val _categories = MutableLiveData<MutableList<String>>()
    private val _isSaved = MutableLiveData<Boolean>(false)
    private val _books = MutableLiveData<List<String>>()

}