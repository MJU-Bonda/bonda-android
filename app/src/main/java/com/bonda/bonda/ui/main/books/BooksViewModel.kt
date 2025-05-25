package com.bonda.bonda.ui.main.books

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.bonda.bonda.R

class BooksViewModel : ViewModel() {
    // private live data
    private val _categoryNovelButtonText = MutableLiveData<String>()
    private val _categoryPoemButtonText = MutableLiveData<String>()
    private val _categoryEssayButtonText = MutableLiveData<String>()
    private val _categoryComicButtonText = MutableLiveData<String>()
    private val _categoryPhotoButtonText = MutableLiveData<String>()
    private val _categoryArtButtonText = MutableLiveData<String>()
    private val _categoryIllustrationButtonText = MutableLiveData<String>()
    private val _categoryMagazineButtonText = MutableLiveData<String>()
    private val _categoryNovelButtonIcon = MutableLiveData<Int>()
    private val _categoryPoemButtonIcon = MutableLiveData<Int>()
    private val _categoryEssayButtonIcon = MutableLiveData<Int>()
    private val _categoryComicButtonIcon = MutableLiveData<Int>()
    private val _categoryPhotoButtonIcon = MutableLiveData<Int>()
    private val _categoryArtButtonIcon = MutableLiveData<Int>()
    private val _categoryIllustrationButtonIcon = MutableLiveData<Int>()
    private val _categoryMagazineButtonIcon = MutableLiveData<Int>()
    private val _recentArrivalBooks = MutableLiveData<List<Book>>()
    private val _mostLovedBooks = MutableLiveData<List<Book>>()


    // read-only properties
    val categoryNovelButtonText: LiveData<String> = _categoryNovelButtonText
    val categoryPoemButtonText: LiveData<String> = _categoryPoemButtonText
    val categoryEssayButtonText: LiveData<String> = _categoryEssayButtonText
    val categoryComicButtonText: LiveData<String> = _categoryComicButtonText
    val categoryPhotoButtonText: LiveData<String> = _categoryPhotoButtonText
    val categoryArtButtonText: LiveData<String> = _categoryArtButtonText
    val categoryIllustrationButtonText: LiveData<String> = _categoryIllustrationButtonText
    val categoryMagazineButtonText: LiveData<String> = _categoryMagazineButtonText
    val categoryNovelButtonIcon: LiveData<Int> = _categoryNovelButtonIcon
    val categoryPoemButtonIcon: LiveData<Int> = _categoryPoemButtonIcon
    val categoryEssayButtonIcon: LiveData<Int> = _categoryEssayButtonIcon
    val categoryComicButtonIcon: LiveData<Int> = _categoryComicButtonIcon
    val categoryPhotoButtonIcon: LiveData<Int> = _categoryPhotoButtonIcon
    val categoryArtButtonIcon: LiveData<Int> = _categoryArtButtonIcon
    val categoryIllustrationButtonIcon: LiveData<Int> = _categoryIllustrationButtonIcon
    val categoryMagazineButtonIcon: LiveData<Int> = _categoryMagazineButtonIcon
    val recentArrivalBooks: LiveData<List<Book>> = _recentArrivalBooks
    val mostLovedBooks: LiveData<List<Book>> = _mostLovedBooks

    // data-class declaration
    data class Book(
        val id: Int,
        val coverImage: Int,
        val category: String,
        val title: String,
        val author: String,
    )


    // init properties
    init {
        _categoryNovelButtonText.value = "소설"
        _categoryPoemButtonText.value = "시집"
        _categoryEssayButtonText.value = "에세이"
        _categoryComicButtonText.value = "만화"
        _categoryPhotoButtonText.value = "사진집"
        _categoryArtButtonText.value = "아트북"
        _categoryIllustrationButtonText.value = "일러스트집"
        _categoryMagazineButtonText.value = "매거진"
        _categoryNovelButtonIcon.value = R.drawable.ic_category_novel
        _categoryPoemButtonIcon.value = R.drawable.ic_category_poem
        _categoryEssayButtonIcon.value = R.drawable.ic_category_essay
        _categoryComicButtonIcon.value = R.drawable.ic_category_comic
        _categoryPhotoButtonIcon.value = R.drawable.ic_category_photobook
        _categoryArtButtonIcon.value = R.drawable.ic_category_artbook
        _categoryIllustrationButtonIcon.value = R.drawable.ic_category_illustrationbook
        _categoryMagazineButtonIcon.value = R.drawable.ic_category_magazine

        _recentArrivalBooks.value = listOf(
            Book(
                id = 1,
                coverImage = R.drawable.dummy_book5,
                category = "에세이",
                title = "어쩐지 제주에 오고 싶었어",
                author = "김희원"
            ),
            Book(
                id = 2,
                coverImage = R.drawable.dummy_book6,
                category = "에세이",
                title = "산책가자",
                author = "배송일"
            ),
            Book(
                id = 3,
                coverImage = R.drawable.dummy_book7,
                category = "에세이",
                title = "파도시집선 019 고백",
                author = "길보배"
            )
        )

        // 요즘 가장 사랑 받은 책 3권
        _mostLovedBooks.value = listOf(
            Book(
                id = 101,
                coverImage = R.drawable.dummy_book8,
                category = "에세이",
                title = "80년대생들의 유서",
                author = "홍글"
            ),
            Book(
                id = 102,
                coverImage = R.drawable.dummy_book9,
                category = "에세이",
                title = "느슨한 성실",
                author = "세모"
            ),
            Book(
                id = 103,
                coverImage = R.drawable.dummy_book10,
                category = "에세이",
                title = "시선이 머무는 순간들",
                author = "천예원"
            )
        )
    }
}