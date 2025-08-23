package com.bonda.bonda.ui.home.articles

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bonda.bonda.AppEvents
import com.bonda.bonda.model.ArticleCategory
import com.bonda.bonda.network.ApiClient
import com.bonda.bonda.util.TAG
import kotlinx.coroutines.launch

class ArticlesViewModel : ViewModel() {
    /**
     * 네트워크 서비스 인스턴스 생성
     */
    private val articleService = ApiClient.articleService
    private var isLoading = false

    /**
     * live-data declaration
     */
    private val _page = MutableLiveData<Int>()
    private val _category = MutableLiveData<String>()
    private val _hasNextPage = MutableLiveData<Boolean>()
    private val _articles = MutableLiveData<List<Article>>()
    private val _isError = MutableLiveData(false)

    /**
     * read-only declaration
     */
    val page: LiveData<Int> = _page
    val category: LiveData<String> = _category
    val hasNextPage: LiveData<Boolean> = _hasNextPage
    val articles: LiveData<List<Article>> = _articles
    val isError: LiveData<Boolean> = _isError

    /**
     * init
     */
    init {
        getArticles()
        observeRefreshEvent()
    }

    /**
     * 아티클 전체를 불러옵니다
     */
    private fun getArticles() {
        viewModelScope.launch {
            try {
                val response = articleService.getArticles(
                    page = 0,
                    size = 10,
                    articleCategory = ArticleCategory.ALL.code
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
                _isError.value = false
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
                _isError.value = true
            }
        }
    }

    /**
     * 아티클 저장 여부를 토글합니다
     */
    suspend fun toggleSaved(articleId: Long): Boolean {
        if (isLoading) return false
        isLoading = true

        try {
            val res = articleService.saveArticle(articleId).unwrapOrThrow()
            _articles.value = _articles.value?.map {
                if (it.id == articleId) it.copy(isSaved = !it.isSaved) else it
            }

            isLoading = false
            _isError.value = false
            return res.isNewBadge
        } catch (e: Exception) {
            isLoading = false
            _isError.value = true
            throw e
        }
    }

    /**
     * 재로드 신호 관찰
     */
    private fun observeRefreshEvent() {
        viewModelScope.launch {
            AppEvents.homeArticlesUpdated.collect {
                getArticles()
            }
        }
    }

}