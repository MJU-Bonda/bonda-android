package com.bonda.bonda.ui.article

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bonda.bonda.network.ApiClient
import com.bonda.bonda.model.TAG
import kotlinx.coroutines.launch

class ArticleViewModel : ViewModel() {
    /**
     * 서비스 인스턴스 선언
     */
    private val articleService = ApiClient.articleService
    private var isSaving = false

    /**
     * live-data 선언
     */
    private val _isLoading = MutableLiveData(true)
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
    private val _hasNewBadge = MutableLiveData(false)

    /**
     * 관찰용 live-data 선언
     */
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
    val hasNewBadge: LiveData<Boolean> = _hasNewBadge

    /**
     * data class 선언
     */
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

    /**
     * 아티클 데이터 조회
     */
    fun getArticleData(articleId: Long) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _isError.value = false

                val response = articleService.getArticleDetail(articleId).unwrapOrThrow()

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
                _hasNewBadge.value = response.isNewBadge
            } catch (e: Exception) {
                Log.e(TAG, "ArticleViewModel.kt::getArticleData()", e)
                _isError.value = true
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * 아티클 저장 토글
     */
    suspend fun toggleSaved(): Boolean {
        if (isSaving)
            return false

        isSaving = true

        val currentlySaved = _isSaved.value ?: return false
        val currentArticleId = _id.value ?: return false

        try {
            val res = articleService.saveArticle(currentArticleId).unwrapOrThrow()
            _isSaved.value = !currentlySaved
            return res.isNewBadge
        } catch (e: Exception) {
            throw e
        } finally {
            isSaving = false
        }
    }

}
