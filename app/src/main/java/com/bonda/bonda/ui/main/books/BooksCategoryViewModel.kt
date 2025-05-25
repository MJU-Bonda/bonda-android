package com.bonda.bonda.ui.main.books

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
        _books.value = listOf(
            Book(1, R.drawable.dummy_book1, "에세이", "마음을 안는 마음", "정현지"),
            Book(2, R.drawable.dummy_book2, "에세이", "샐러드 먹는 날", "김태연"),
            Book(3, R.drawable.dummy_book3, "에세이", "밥은 굶어도 미술관은 갈 거야", "재니"),
            Book(4, R.drawable.dummy_book4, "에세이", "열심히 일하느라 아무것도 몰랐어", "김두배"),
            Book(5, R.drawable.dummy_book5, "기술", "코틀린의 즐거움", "이민호"),
            Book(6, R.drawable.dummy_book6, "기술", "안드로이드 정복", "박지영"),
            Book(7, R.drawable.dummy_book7, "교육", "데이터 과학 입문", "최수현"),
            Book(8, R.drawable.dummy_book8, "교육", "머신러닝 A to Z", "김민수"),
            Book(9, R.drawable.dummy_book9, "디자인", "아름다운 디자인", "송지효"),
            Book(10, R.drawable.dummy_book10, "여행", "여행의 기술", "정다울"),
            Book(11, R.drawable.dummy_book11, "요리", "맛있는 요리 레시피", "하준"),
            Book(12, R.drawable.dummy_book12, "소설", "아무개 이야기", "오세훈"),
            Book(13, R.drawable.dummy_book13, "소설", "미래를 꿈꾸며", "배수지"),
            Book(14, R.drawable.dummy_book14, "철학", "철학의 초상", "유재석"),
            Book(15, R.drawable.dummy_book15, "기술", "소프트웨어 공학", "류호석"),
            Book(16, R.drawable.dummy_book16, "에세이", "세상 속으로", "전지현")
        )
    }
}