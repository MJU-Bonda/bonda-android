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
    private var isLoading = false

    /**
     * live-data declaration
     */
    private val _page = MutableLiveData<Int>()
    private val _category = MutableLiveData<String>()
    private val _hasNextPage = MutableLiveData<Boolean>()
    private val _articles = MutableLiveData<List<Article>>()

    /**
     * read-only declaration
     */
    val page: LiveData<Int> = _page
    val category: LiveData<String> = _category
    val hasNextPage: LiveData<Boolean> = _hasNextPage
    val articles: LiveData<List<Article>> = _articles

    /**
     * 카테고리별 아티클을 불러옵니다
     */
    fun getArticlesByCategory(category: String) {
        viewModelScope.launch {
            try {
                val response = articleService.getArticles(
                    page = 0,
                    size = 10,
                    articleCategory = category
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
            return res.isNewBadge
        } catch (e: Exception) {
            isLoading = false
            throw e
        }
    }

}