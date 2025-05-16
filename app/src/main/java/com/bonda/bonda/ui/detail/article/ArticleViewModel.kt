package com.bonda.bonda.ui.detail.article

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bonda.bonda.R

class ArticleViewModel : ViewModel() {
    private val _id = MutableLiveData<Int>()
    private val _isSaved = MutableLiveData<Boolean>()
    private val _coverImage = MutableLiveData<Int>()
    private val _category = MutableLiveData<String>()
    private val _title = MutableLiveData<String>()
    private val _subTitle = MutableLiveData<String>()
    private val _body = MutableLiveData<String>()
    private val _books = MutableLiveData<List<Book>>()
    private val _articles = MutableLiveData<List<Article>>()

    // read-only properties
    val id: LiveData<Int> = _id
    val isSaved: LiveData<Boolean> = _isSaved
    val coverImage: LiveData<Int> = _coverImage
    val category: LiveData<String> = _category
    val title: LiveData<String> = _title
    val subTitle: LiveData<String> = _subTitle
    val body: LiveData<String> = _body
    val books: LiveData<List<Book>> = _books
    val articles: LiveData<List<Article>> = _articles

    // data-class declaration
    data class Book(
        val id: Int,
        val coverImage: Int,
        val category: String,
        val title: String,
        val author: String,
        val body: String
    )
    data class Article(
        val id: Int,
        val coverImage: Int,
        val category : String,
        val title: String
    )

    // -------------------------------------------- >8 --------------------------------------------
    // dummy-init code
    init {
        _id.value = 1
        _isSaved.value = false
        _coverImage.value = R.drawable.dummy_article_cover2
        _category.value = "테마"
        _title.value = "한 가지 사물,\n수백 가지 이야기"
        _subTitle.value = "한 가지 사물에 숨겨진 다채로운 순간들"
        _body.value = """
            작은 사물 하나에도 수많은 이야기가 숨어 있습니다. 하지만, 우리는 그 사실을 잊을 때가 많죠.
            한 가지 사물에 집중해 다양한 형태와 상황을 담아낸 이 포토북 시리즈는 일상에서 흔히 지나치기 쉬운 물건들이 작가의 시선을 통해 특별한 이야기를 품고 재탄생합니다.
            
            각 책은 사물을 새롭게 해석한 작품이자, 순간의 아름다움을 포착한 예술 작품이죠.
            일상을 특별하게 기록한 이 포토북 시리즈를 통해 감각적인 시선과 유쾌한 상상력을 경험해보세요.
            『하트책』은 일상 속 하트 모양을 포착해 사랑스러운 순간을 담아내고, 『가구 산』은 가구가 빚어내는 독특한 풍경을, 『오로지 계란후라이』는 단순하지만 다채로운 계란후라이의 변주를 보여줍니다. 그리고 『엿책』은 살짝 짓궂지만 재치 있는 시선으로 ‘엿’을 포착해, 예상치 못한 상황에서 위트 있는 웃음을 선사합니다.
            
            기발한 발상과 사진가의 유쾌한 시선이 돋보이는 이 포토북은 일상 속 물건을 새로운 시각으로 바라보게 만들 거예요.
        """.trimIndent()
        _books.value = listOf(
            Book(1, R.drawable.dummy_book1, "테마", "하트책", "홍길동", "사랑스러운 하트 모양을 담은 포토북"),
            Book(2, R.drawable.dummy_book2, "테마", "가구 산", "김철수", "가구가 만들어 내는 풍경을 포착한 포토북"),
            Book(3, R.drawable.dummy_book3, "테마", "오로지 계란후라이", "이영희", "계란후라이의 다양한 얼굴을 담은 포토북"),
            Book(4, R.drawable.dummy_book4, "테마", "엿책", "박민수", "엿의 재치 있는 순간을 담은 포토북")
        )
        _articles.value = listOf(
            Article(1, R.drawable.dummy_article_cover1, "작가/출판사", "오수영 작가의 사색과 감성"),
            Article(2, R.drawable.dummy_article_cover4, "테마", "함께 살아가는 따뜻 이야기"),
            Article(3, R.drawable.dummy_article_cover3, "테마", "집, 우리 삶의 거울"),
        )
    }
    // -------------------------------------------- >8 --------------------------------------------
}
