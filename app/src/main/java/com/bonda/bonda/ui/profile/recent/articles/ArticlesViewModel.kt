package com.bonda.bonda.ui.profile.recent.articles

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bonda.bonda.network.ApiClient
import com.bonda.bonda.util.TAG
import kotlinx.coroutines.launch

class ArticlesViewModel : ViewModel() {

    companion object {
        private const val PAGE_SIZE = 24
    }

    private val articleService = ApiClient.articleService
    private var page = 0

    private val _isLoading = MutableLiveData(false)
    private val _error    = MutableLiveData(false)
    private val _articles = MutableLiveData<List<Article>>(emptyList())
    private val _hasNextPage = MutableLiveData(false)

    val isLoading: LiveData<Boolean> = _isLoading
    val error: LiveData<Boolean> = _error
    val articles: LiveData<List<Article>> = _articles
    val hasNextPage: LiveData<Boolean> = _hasNextPage

    fun getArticles() {
        viewModelScope.launch {
            _isLoading.value = true

            try {
                val res = articleService.getRecentViewedArticles(
                    page,
                    PAGE_SIZE,
                ).unwrapOrThrow()

                Log.d(TAG, res.toString())

                val newArticles = res.articleList.map { article ->
                    Article(
                        id = article.articleId,
                        imageUrl = article.imageUrl,
                        category = article.articleCategory,
                        title = article.title
                    )
                }
                _hasNextPage.value = res.hasNextPage

                val current = _articles.value.orEmpty()
                _articles.value = current + newArticles
            } catch (e: Exception) {
                Log.e(TAG, e.message.toString())
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getNextPage() {
        if (_hasNextPage.value != true) return

        page++
        getArticles()
    }

}
