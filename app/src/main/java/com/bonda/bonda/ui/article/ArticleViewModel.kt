package com.bonda.bonda.ui.article

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ArticleViewModel : ViewModel() {
    private val _isSaved = MutableLiveData<Boolean>(false)
    private val _categories = MutableLiveData<List<String>>()
    private val _title = MutableLiveData<String>("한 가지 사물, 수백 가지 이야기")
    private val _subTitle = MutableLiveData<String>("한 가지 사물에 숨겨진 다채로운 순간들")
    private val _body = MutableLiveData<String>("""
        작은 사물 하나에도 수많은 이야기가 숨어 있습니다. 하지만, 우리는 그 사실을 잊을 때가 많죠.
        한 가지 사물에 집중해 다양한 형태와 상황을 담아낸 이 포토북 시리즈는 일상에서 흔히 지나치기 쉬운 물건들이 작가의 시선을 통해 특별한 이야기를 품고 재탄생합니다.
        
        각 책은 사물을 새롭게 해석한 작품이자, 순간의 아름다움을 포착한 예술 작품이죠.
        일상을 특별하게 기록한 이 포토북 시리즈를 통해 감각적인 시선과 유쾌한 상상력을 경험해보세요.
        『하트책』은 일상 속 하트 모양을 포착해 사랑스러운 순간을 담아내고, 『가구 산』은 가구가 빚어내는 독특한 풍경을, 『오로지 계란후라이』는 단순하지만 다채로운 계란후라이의 변주를 보여줍니다. 그리고 『엿책』은 살짝 짓궂지만 재치 있는 시선으로 ‘엿’을 포착해, 예상치 못한 상황에서 위트 있는 웃음을 선사합니다.
        
        기발한 발상과 사진가의 유쾌한 시선이 돋보이는 이 포토북은 일상 속 물건을 새로운 시각으로 바라보게 만들 거예요.
    """.trimIndent())
    private val _books = MutableLiveData<List<Map<String, String>>>().apply {
        value = listOf(
            mapOf(
                "image" to "이미지주소",

                "title" to "하트책",
                "author" to "이혜승",

            )
        )
    }

    // read-only variables
    val isSaved: LiveData<Boolean> = _isSaved
    val categories: LiveData<List<String>> = _categories
    val title: LiveData<String> = _title
    val subTitle: LiveData<String> = _subTitle
    val body: LiveData<String> = _body
    val books: LiveData<List<Map<String, String>>> = _books




}