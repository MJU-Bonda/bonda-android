package com.bonda.bonda.ui.home.articles

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bonda.bonda.model.AppEvents
import com.bonda.bonda.model.ArticleCategory
import com.bonda.bonda.network.ApiClient
import com.bonda.bonda.model.TAG
import kotlinx.coroutines.launch

class ArticlesViewModel : ViewModel() {
    /**
     * 네트워크 서비스 인스턴스 생성
     */
    private val articleService = ApiClient.articleService
    private var isSaving = false

    /**
     * live-data declaration
     */
    private val _isLoading = MutableLiveData(true)
    private val _isError = MutableLiveData(false)
    private val _page = MutableLiveData<Int>()
    private val _category = MutableLiveData<String>()
    private val _hasNextPage = MutableLiveData<Boolean>()
    private val _articles = MutableLiveData<List<Article>>()

    /**
     * read-only declaration
     */
    val isLoading: LiveData<Boolean> = _isLoading
    val isError: LiveData<Boolean> = _isError
    val page: LiveData<Int> = _page
    val category: LiveData<String> = _category
    val hasNextPage: LiveData<Boolean> = _hasNextPage
    val articles: LiveData<List<Article>> = _articles

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
    fun getArticles() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _isError.value = false

                val response = articleService.getArticles(
                    page = 0,
                    size = 10,
                    articleCategory = ArticleCategory.ALL.code
                ).unwrapOrThrow()

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
                Log.e(TAG, "ArticlesViewModel.kt::getArticles()", e)
                _isError.value = true
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * 아티클 저장 여부를 토글합니다
     */
    suspend fun toggleSaved(articleId: Long): Boolean {
        if (isSaving)
            return false

        isSaving = true

        try {
            val res = articleService.saveArticle(articleId).unwrapOrThrow()
            _articles.value = _articles.value?.map {
                if (it.id == articleId) it.copy(isSaved = !it.isSaved) else it
            }

            _isError.value = false
            return res.isNewBadge
        } catch (e: Exception) {
            _isError.value = true
            throw e
        } finally {
            isSaving = false
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