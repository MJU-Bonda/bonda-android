package com.bonda.bonda.ui.main.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bonda.bonda.R

class HomeViewModel : ViewModel() {
    private val _articles = MutableLiveData<List<Article>>()

    // read-only properties
    val articles: LiveData<List<Article>> = _articles

    // data-class declaration
    data class Article(
        val id: Int,
        val isSaved: Boolean,
        val coverImage: Int,
        val category: String,
        val title: String,
        val subTitle: String
    )

    // dummy-init code
    init {
        _articles.value = listOf(
            Article(1, false, R.drawable.dummy_article_cover1, "작가/출판사", "오수영 작가의\n사색과 감성", "삶의 순간들을 섬세하게 드려내는 에세이스트"),
            Article(2, true, R.drawable.dummy_article_cover2, "테마", "한 가지 사물,\n수백 가지 이야기", "사소한 물건들의 다채로운 순간들"),
            Article(3, false, R.drawable.dummy_article_cover3, "테마", "집, 우리 삶의 거울", "공간에 담긴 삶과 생각의 흔적들"),
            Article(4, false, R.drawable.dummy_article_cover4, "테마", "함께 살아가는\n따뜻한 이야기", "반려동물이 전하는 사랑과 위로의 순간들"),
            Article(5, false, R.drawable.dummy_article_cover5, "테마", "커피를 좋아하는\n당신에게", "커피 한 잔 속에서 피어나는 소소한 순간들")
        )
    }
}