package com.bonda.bonda.ui.home.articles

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bonda.bonda.network.ApiClient
import com.bonda.bonda.util.TAG
import kotlinx.coroutines.launch

class ArticlesViewModel : ViewModel() {

    /**
     * 네트워크 서비스 인스턴스 생성
     */
    private val articleService = ApiClient.articleService

    /**
     *
     */
    private val _page = MutableLiveData<Int>()
    private val _category = MutableLiveData<String>()
    private val _hasNextPage = MutableLiveData<Boolean>()
    private val _articles = MutableLiveData<List<Article>>()

    /**
     * read-only properties
     */
    val page: LiveData<Int> = _page
    val category: LiveData<String> = _category
    val hasNextPage: LiveData<Boolean> = _hasNextPage
    val articles: LiveData<List<Article>> = _articles

    /**
     * data-class declaration
     */
    data class Article(
        val id: Long,
        val isSaved: Boolean,
        val coverImage: String,
        val category: String,
        val title: String,
        val subTitle: String
    )

    init {
        viewModelScope.launch {
            try {
                val response = articleService.getArticles(
                    page = 0,
                    size = 10,
                    articleCategory = "ALL"
                ).unwrapOrThrow()

                Log.d(TAG, response.toString())

                /**
                 * 데이터 formatting
                 */
                _page.value = response.page
                _category.value = response.category
                _hasNextPage.value = response.hasNextPage
                _articles.value = response.articleList.map { item ->
                    Article(
                        id = item.articleId,
                        isSaved = item.isBookmarked,
                        coverImage = item.imageUrl,
                        category = item.category,
                        title = item.title,
                        subTitle = item.introduction
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
                // TODO 예외처리 만들자
            }
        }
    }

    fun toggleSaved(articleId: Long) {
        viewModelScope.launch {
            val currentList = _articles.value
            val target = currentList!!.find { it.id == articleId } ?: return@launch
            val isSaved = target.isSaved

            try {
                if (!isSaved) {
                    articleService.saveArticle(articleId)
                } else {
                    articleService.deleteSavedArticle(articleId)
                }

                _articles.value = _articles.value?.map {
                    if (it.id == articleId) it.copy(isSaved = !it.isSaved) else it
                }
            } catch (e: Exception) {
                Log.e(TAG, e.message.toString())
            }
        }
    }
}