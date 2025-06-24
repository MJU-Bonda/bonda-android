package com.bonda.bonda.ui.book

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bonda.bonda.R

class BookViewModel : ViewModel() {
    private val _id = MutableLiveData<Int>()
    private val _isSaved = MutableLiveData<Boolean>()
    private val _coverImage = MutableLiveData<Int>()
    private val _category = MutableLiveData<String>()
    private val _title = MutableLiveData<String>()
    private val _author = MutableLiveData<String>()
    private val _publisher = MutableLiveData<String>()
    private val _size = MutableLiveData<String>()
    private val _pageLength = MutableLiveData<Int>()
    private val _theme = MutableLiveData<String?>()
    private val _body = MutableLiveData<String>()
    private val _articles = MutableLiveData<List<Article>>()

    // read-only properties
    val id: LiveData<Int> = _id
    val isSaved: LiveData<Boolean> = _isSaved
    val coverImage: LiveData<Int> = _coverImage
    val category: LiveData<String> = _category
    val title: LiveData<String> = _title
    val author: LiveData<String> = _author
    val publisher: LiveData<String> = _publisher
    val size: LiveData<String> = _size
    val pageLength: LiveData<Int> = _pageLength
    val theme: LiveData<String?> = _theme
    val body: LiveData<String> = _body
    val articles: LiveData<List<Article>> = _articles

    // data-class declaration
    data class Article(
        val id: Int,
        val coverImage: Int,
        val category: String,
        val title: String
    )

    // dummy-init code
    init {
        _id.value = 1
        _isSaved.value = true
        _coverImage.value = R.drawable.dummy_book11
        _category.value = "사진"
        _title.value = "하트책"
        _author.value = "이혜승"
        _publisher.value = "곳"
        _size.value = "105 * 148mm"
        _pageLength.value = 200
        _theme.value = "미니멀리즘" // 또는 null 값이 올수 있음
        _body.value = "길을 걷다 문득 마주친 하트 모양, 우연히 발견한 작은 순간들을 기록한 사진집이에요.\n" +
                "\n" +
                " 다양한 형태의 사랑이 존재하는 것처럼, 하트도 가지각색 분위기로 존재하는데요. 꾸준히 모아온 이 책 속 하트들은 보는 것만으로도 마음을 따뜻하게 만들어줍니다. 책장을 넘기다 보면 어느새 사랑스러운 감정이 스며들고, 작은 하트 하나에도 기분이 밝아지는 신비한 경험을 하게 될 거예요. \n" +
                " 이 책에는 100여 개의 하트가 담겨 있어요. 하트는 단순한 모양이 아니라, 사랑과 감정을 전하는 강력한 상징이잖아요. 누군가에게 선물하며 마음을 나누어도, 나 자신에게 작은 행복을 선물해도 좋겠죠.\n" +
                " 책을 펼치는 순간, 단순한 사진집이 아니라 따뜻한 감정이 가득 담긴 한 권의 편지가 됩니다. 마음속에 작은 하트를 품고 싶은 날, 이 책이 당신의 일상을 더욱 사랑스럽게 만들어줄 거예요. ♡"
        _articles.value = emptyList<Article>()

//        listOf(
//            Article(2, R.drawable.dummy_article_cover4, "테마", "함께 살아가는 따뜻 이야기")
//        )
    }
}