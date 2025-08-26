package com.bonda.bonda.ui.book

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bonda.bonda.network.ApiClient
import com.bonda.bonda.model.TAG
import kotlinx.coroutines.launch

class BookViewModel : ViewModel() {
    /**
     * 네트워크 인스턴스
     */
    private val bookService = ApiClient.bookService

    private var isLoading = false

    /**
     * 라이브 데이터
     */
    private val _isError = MutableLiveData(false)
    private val _id = MutableLiveData<Long>()
    private val _isSaved = MutableLiveData<Boolean>()
    private val _coverImage = MutableLiveData<String>()
    private val _category = MutableLiveData<String>()
    private val _title = MutableLiveData<String>()
    private val _author = MutableLiveData<String>()
    private val _publisher = MutableLiveData<String>()
    private val _size = MutableLiveData<String>()
    private val _pageLength = MutableLiveData<Int>()
    private val _theme = MutableLiveData<String?>()
    private val _body = MutableLiveData<String>()
    private val _articles = MutableLiveData<List<Article>>()

    /**
     * 읽기 전용 데이터
     */
    val isError: LiveData<Boolean> = _isError
    val id: LiveData<Long> = _id
    val isSaved: LiveData<Boolean> = _isSaved
    val coverImage: LiveData<String> = _coverImage
    val category: LiveData<String> = _category
    val title: LiveData<String> = _title
    val author: LiveData<String> = _author
    val publisher: LiveData<String> = _publisher
    val size: LiveData<String> = _size
    val pageLength: LiveData<Int> = _pageLength
    val theme: LiveData<String?> = _theme
    val body: LiveData<String> = _body
    val articles: LiveData<List<Article>> = _articles

    /**
     * 데이터 클래스
     */
    data class Article(
        val id: Long,
        val coverImage: String,
        val category: String,
        val title: String
    )

    /**
     * 도서 상세 정보를 조회합니다
     */
    fun getBookDetail(bookId: Long) {
        isLoading = true

        viewModelScope.launch {
            try {
                val res = bookService.getBookDetail(bookId).unwrapOrThrow()

                _id.value = res.bookId
                _isSaved.value = res.isBookmarked
                _coverImage.value = res.imageUrl
                _category.value = res.category
                _title.value = res.title
                _author.value = res.author
                _publisher.value = res.publisher
                _size.value = res.plateType
                _pageLength.value = res.page
                _theme.value = res.subject
                _body.value = res.content
                _articles.value = res.relatedArticleList.map { article ->
                    Article(
                        id = article.articleId,
                        coverImage = article.imageUrl,
                        category = article.articleCategory,
                        title = article.title
                    )
                }
                _isError.value = false
            } catch (e: Exception) {
                Log.e(TAG, "BookViewModel.kt::getBookDetail", e)
                _isError.value = true
            } finally {
                isLoading = false
            }
        }
    }

    /**
     * 도서 북마크 여부를 토글합니다
     */
    suspend fun toggleSaveBook(bookId: Long): Boolean {
        if (isLoading) return false
        isLoading = true

        try {
            val res = bookService.toggleSaveBook(bookId).unwrapOrThrow().isNewBadge
            val current = _isSaved.value
            _isSaved.value = !current!!
            _isError.value = false
            return res
        } catch (e: Exception) {
            _isError.value = true
            throw e
        } finally {
            isLoading = false
        }
    }

}