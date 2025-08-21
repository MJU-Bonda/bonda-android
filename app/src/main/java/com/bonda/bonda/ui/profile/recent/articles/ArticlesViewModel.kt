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
    /**
     * pagination을 구현하지 않고 최대 로드 길이를 256으로 설정합니다
     */
    companion object {
        private const val PAGE_SIZE = 256
    }

    private val articleService = ApiClient.articleService
    private var page = 0

    private val _isLoading = MutableLiveData(false)
    private val _isError = MutableLiveData(false)
    private val _isEmpty = MutableLiveData(false)
    private val _articles = MutableLiveData<List<Article>>(emptyList())
    private val _hasNextPage = MutableLiveData(false)

    val isLoading: LiveData<Boolean> = _isLoading
    val isError: LiveData<Boolean> = _isError
    val isEmpty: LiveData<Boolean> = _isEmpty
    val articles: LiveData<List<Article>> = _articles
    val hasNextPage: LiveData<Boolean> = _hasNextPage

    fun getArticles() {
        viewModelScope.launch {
            _isLoading.value = true

            try {
                val res = articleService
                    .getRecentViewedArticles(page, PAGE_SIZE)
                    .unwrapOrThrow()

                val current = _articles.value.orEmpty()
                val newArticles = res.articleList.map { article ->
                    Article(
                        id = article.articleId,
                        imageUrl = article.imageUrl,
                        category = article.articleCategory,
                        title = article.title
                    )
                }
                val updated = current + newArticles

                _articles.value = updated
                _isEmpty.value = updated.isEmpty()
                _hasNextPage.value = res.hasNextPage
                _isError.value = false
            } catch (e: Exception) {
                Log.e(TAG, e.message.toString())
                _isError.value = true
            } finally {
                _isLoading.value = false
            }
        }
    }
}
