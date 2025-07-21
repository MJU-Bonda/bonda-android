package com.bonda.bonda.ui.book

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bonda.bonda.R
import com.bonda.bonda.network.ApiClient
import com.bonda.bonda.network.model.book.SaveBookResponse
import com.bonda.bonda.ui.profile.recent.books.Book
import com.bonda.bonda.util.TAG
import kotlinx.coroutines.launch

class BookViewModel : ViewModel() {

    private val bookService = ApiClient.bookService

    private val _isLoading = MutableLiveData(false)
    private val _error = MutableLiveData(false)
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

    val isLoading: LiveData<Boolean> = _isLoading
    val error: LiveData<Boolean> = _error
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

    data class Article(
        val id: Long,
        val coverImage: String,
        val category: String,
        val title: String
    )

    fun getBookDetail(bookId: Long) {
        _isLoading.value = true

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
            } catch (e: Exception) {
                Log.e(TAG, e.message.toString())
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun toggleSaveBook(bookId: Long): Boolean {
        if (_isLoading.value == true) return false
        _isLoading.value = true

        var hasNewBadge = false

        viewModelScope.launch {
            try {
                val res = bookService.toggleSaveBook(bookId).unwrapOrThrow()
                Log.d(TAG, res.toString())

                val current = _isSaved.value
                _isSaved.value = !current!!
                hasNewBadge = res.isNewBadge
            } catch (e: Exception) {
                Log.e(TAG, e.message.toString())
            } finally {
                _isLoading.value = false
            }
        }

        return hasNewBadge
    }
}