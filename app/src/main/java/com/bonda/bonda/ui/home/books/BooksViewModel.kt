package com.bonda.bonda.ui.home.books

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bonda.bonda.R
import com.bonda.bonda.model.BookCategory
import com.bonda.bonda.model.BookTheme
import com.bonda.bonda.network.ApiClient
import com.bonda.bonda.util.TAG
import kotlinx.coroutines.launch

class BooksViewModel : ViewModel() {
    /**
     * network service 선언
     */
    private val bookService = ApiClient.bookService

    /**
     * view model 데이터 선언
     */
    private val _selectedNewArrivedBooksCategory = MutableLiveData(BookCategory.ALL.code)
    private val _selectedMostLovedBooksCategory = MutableLiveData(BookTheme.ALL.code)

    /**
     * 관찰용 live data
     */
    val selectedNewArrivedBooksCategory: LiveData<String> = _selectedNewArrivedBooksCategory
    val selectedMostLovedBooksCategory: LiveData<String> = _selectedMostLovedBooksCategory

    /**
     * 삭제 예정
     */
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

    data class Book(
        val id: Long,
        val coverImage: String,
        val category: String,
        val title: String,
        val author: String,
    )


    /**
     * 영구 관찰
     */
    init {
        _selectedNewArrivedBooksCategory.observeForever { getNewArrivedBooks(it) }
        _selectedMostLovedBooksCategory.observeForever { getMostLovedBooks(it) }

        /**
         * 삭제 예정 코드
         */
        _categoryNovelButtonText.value = "소설"
        _categoryPoemButtonText.value = "시집"
        _categoryEssayButtonText.value = "에세이"
        _categoryComicButtonText.value = "만화"
        _categoryPhotoButtonText.value = "사진집"
        _categoryArtButtonText.value = "아트북"
        _categoryIllustrationButtonText.value = "일러스트집"
        _categoryMagazineButtonText.value = "매거진"
        _categoryNovelButtonIcon.value = R.drawable.ic_category_novel_24dp
        _categoryPoemButtonIcon.value = R.drawable.ic_category_poem_24dp
        _categoryEssayButtonIcon.value = R.drawable.ic_category_essay_24dp
        _categoryComicButtonIcon.value = R.drawable.ic_category_comic_24dp
        _categoryPhotoButtonIcon.value = R.drawable.ic_category_photobook_24dp
        _categoryArtButtonIcon.value = R.drawable.ic_category_artbook_24dp
        _categoryIllustrationButtonIcon.value = R.drawable.ic_category_illustrationbook_24dp
        _categoryMagazineButtonIcon.value = R.drawable.ic_category_magazine_24dp
    }

    /**
     * 방금 도착한 새로운 책 조회
     */
    fun getNewArrivedBooks(category: String) {
        viewModelScope.launch {
            try {
                val res = bookService.getBooksByCategory(
                    size = 3,
                    category = category
                ).unwrapOrThrow()

                Log.d(TAG, res.toString())

                _recentArrivalBooks.value = res.bookList.map {
                    Book(
                        id = it.id,
                        coverImage = it.imageUrl,
                        category = it.category,
                        title = it.title,
                        author = it.author
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, e.message.toString())
            }
        }
    }

    /**
     * 요즘 가장 많이 사랑 받은 책 조회
     */
    fun getMostLovedBooks(category: String) {
        viewModelScope.launch {
            try {
                val res = bookService.getMostLovedBooks(
                    subject = category
                ).unwrapOrThrow()

                Log.d(TAG, res.toString())

                _mostLovedBooks.value = res.bookList.map {
                    Book(
                        id = it.id,
                        coverImage = it.imageUrl,
                        category = it.category,
                        title = it.title,
                        author = it.author
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, e.message.toString())
            }
        }
    }

    fun setSelectedMostLovedBooksCategory(category: String) {
        _selectedMostLovedBooksCategory.value = category
    }

    fun setSelectedNewArrivedBooksCategory(category: String) {
        _selectedNewArrivedBooksCategory.value = category
    }


}