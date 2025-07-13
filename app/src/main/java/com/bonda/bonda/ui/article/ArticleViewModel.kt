package com.bonda.bonda.ui.article

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bonda.bonda.model.ArticleCategory
import com.bonda.bonda.network.ApiClient
import com.bonda.bonda.util.TAG
import kotlinx.coroutines.launch

class ArticleViewModel : ViewModel() {

    private val articleService = ApiClient.articleService

    private val _isLoading = MutableLiveData(false)
    private val _isError = MutableLiveData(false)
    private val _id = MutableLiveData<Long>()
    private val _isSaved = MutableLiveData<Boolean>()
    private val _coverImage = MutableLiveData<String>()
    private val _category = MutableLiveData<String>()
    private val _title = MutableLiveData<String>()
    private val _subTitle = MutableLiveData<String>()
    private val _body = MutableLiveData<String>()
    private val _books = MutableLiveData<List<Book>>()
    private val _articles = MutableLiveData<List<Article>>()

    val isLoading: LiveData<Boolean> = _isLoading
    val isError: LiveData<Boolean> = _isError
    val id: LiveData<Long> = _id
    val isSaved: LiveData<Boolean> = _isSaved
    val coverImage: LiveData<String> = _coverImage
    val category: LiveData<String> = _category
    val title: LiveData<String> = _title
    val subTitle: LiveData<String> = _subTitle
    val body: LiveData<String> = _body
    val books: LiveData<List<Book>> = _books
    val articles: LiveData<List<Article>> = _articles

    data class Book(
        val id: Long,
        val coverImage: String,
        val category: String,
        val title: String,
        val author: String,
        val body: String
    )

    data class Article(
        val id: Long,
        val coverImage: String,
        val category: String,
        val title: String
    )

    fun getArticleData(articleId: Long) {
        _isLoading.value = true

        viewModelScope.launch {
            try {
                val response = articleService.getArticleDetail(articleId).unwrapOrThrow()
                Log.d(TAG, response.toString())

                _id.value = response.articleId
                _isSaved.value = response.isBookmarked
                _coverImage.value = response.imageUrl
                _category.value = response.articleCategory
                _title.value = response.title
                _subTitle.value = response.introduction
                _body.value = response.content
                _books.value = response.relatedBookList.map { item ->
                    Book(
                        id = item.bookId,
                        coverImage = item.imageUrl,
                        category = item.category,
                        title = item.title,
                        author = item.author,
                        body = item.content
                    )
                }
                _articles.value = response.otherArticleList.map { item ->
                    Article(
                        id = item.articleId,
                        coverImage = item.imageUrl,
                        category = item.articleCategory,
                        title = item.title
                    )
                }

                _isError.value = false
            } catch (e: Exception) {
                Log.e(TAG, e.message.toString())

                _isError.value = true
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun toggleSaved() {
        val currentlySaved = _isSaved.value ?: return
        val currentArticleId = _id.value ?: return

        viewModelScope.launch {
            try {
                articleService.saveArticle(currentArticleId)

                _isSaved.value = !currentlySaved
            } catch (e: Exception) {
                Log.e(TAG, e.message.toString())
            }
        }
    }
}
