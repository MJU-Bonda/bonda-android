package com.bonda.bonda.ui.search

import android.app.Application
import android.util.Log
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bonda.bonda.network.ApiClient
import com.bonda.bonda.model.PREFS_NAME
import com.bonda.bonda.model.PREF_KEY_SEARCH_HISTORY_ACTIVATED
import com.bonda.bonda.model.TAG
import kotlinx.coroutines.launch
import androidx.core.content.edit

class SearchViewModel(application: Application) : AndroidViewModel(application) {

    private val searchService = ApiClient.searchService
    private var bookPage = 0
    private var articlePage = 0
    private var searchKeyword = ""

    private val prefs = application.getSharedPreferences(PREFS_NAME, MODE_PRIVATE)

    private var isLoading = false
    private val _isSearchHistoryEmpty = MutableLiveData<Boolean>()
    private val _isHistoryActivated = MutableLiveData<Boolean>()
    private val _searchHistory = MutableLiveData<List<String>>()
    private val _recommendedKeyword = MutableLiveData<List<String>>()
    private val _booksSearchResult = MutableLiveData<List<Book>>(emptyList())
    private val _articlesSearchResult = MutableLiveData<List<Article>>(emptyList())
    private val _booksHasNextPage = MutableLiveData<Boolean>(false)
    private val _articlesHasNextPage = MutableLiveData<Boolean>(false)
    private val _booksSearchResultCount = MutableLiveData<Int>(0)
    private val _articlesSearchResultCount = MutableLiveData<Int>(0)

    val isSearchHistoryEmpty: LiveData<Boolean> = _isSearchHistoryEmpty
    val isHistoryActivated: LiveData<Boolean> = _isHistoryActivated
    val searchHistory: LiveData<List<String>> = _searchHistory
    val recommendedKeyword: LiveData<List<String>> = _recommendedKeyword
    val booksSearchResult: LiveData<List<Book>> = _booksSearchResult
    val articlesSearchResult: LiveData<List<Article>> = _articlesSearchResult
    val booksHasNextPage: LiveData<Boolean> = _booksHasNextPage
    val articlesHasNextPage: LiveData<Boolean> = _articlesHasNextPage
    val booksSearchResultCount: LiveData<Int> = _booksSearchResultCount
    val articlesSearchResultCount: LiveData<Int> = _articlesSearchResultCount

    init {
        _isHistoryActivated.value = prefs.getBoolean(PREF_KEY_SEARCH_HISTORY_ACTIVATED, false)
        loadSearchHistory()
        loadRecommendedKeyword()
    }

    /**
     * 검색 기능 연결
     */
    fun search(keyword: String) {
        searchKeyword = keyword
        searchBooks(keyword)
        searchArticles(keyword)
        loadSearchHistory()
    }

    fun searchBooks(keyword: String) {
        viewModelScope.launch {
            try {
                val res = searchService
                    .searchBooks(page = bookPage, word = keyword)
                    .unwrapOrThrow()

                val current = _booksSearchResult.value.orEmpty()
                val newBooks = res.bookList.map { book ->
                    Book(
                        id = book.id,
                        imageUrl = book.imageUrl,
                        category = book.category,
                        title = book.title,
                        subtitle = book.author
                    )
                }

                val updated = current + newBooks

                _booksSearchResult.value = updated
                _booksHasNextPage.value = res.hasNextPage
                _booksSearchResultCount.value = res.total

                Log.d(TAG, "도서 검색 결과 ${_booksSearchResult.value.toString()}")
            } catch (e: Exception) {
                Log.e(TAG, e.message.toString())
            }
        }
    }

    fun getNextBooks() {
        if (_booksHasNextPage.value != true) return
        bookPage++
        searchBooks(searchKeyword)
    }

    fun searchArticles(keyword: String) {
        viewModelScope.launch {
            try {
                val res = searchService
                    .searchArticles(page = articlePage, word = keyword)
                    .unwrapOrThrow()

                val current = _articlesSearchResult.value.orEmpty()
                val newArticles = res.articleList.map { article ->
                    Article(
                        id = article.articleId,
                        imageUrl = article.imageUrl,
                        category = article.articleCategory,
                        title = article.title
                    )
                }

                val updated = current + newArticles

                _articlesSearchResult.value = updated
                _articlesHasNextPage.value = res.hasNextPage
                _articlesSearchResultCount.value = res.total

                Log.d(TAG, "아티클 검색 결과 ${_articlesSearchResult.value.toString()}")
            } catch (e: Exception) {
                Log.e(TAG, e.message.toString())
            }
        }
    }

    fun getNextArticles() {
        if (_articlesHasNextPage.value != true) return
        articlePage++
        searchArticles(searchKeyword)
    }


    /**
     * 특정 검색어 삭제
     */
    fun removeSearchHistory(keyword: String) {
        if (isLoading) return

        viewModelScope.launch {
            try {
                searchService.deleteSearchHistory(keyword)

                _searchHistory.value = _searchHistory.value
                    ?.filterNot { it == keyword }
                _isSearchHistoryEmpty.value = _searchHistory.value!!.isEmpty()
            } catch (e: Exception) {
                Log.e(TAG, e.message.toString())
            }
        }
    }


    /**
     * 검색 기록 불러오기
     */
    private fun loadSearchHistory() {
        viewModelScope.launch {
            try {
                isLoading = true

                val response = searchService.getSearchHistory().unwrapOrThrow()

                _isSearchHistoryEmpty.value = response.recentSearchTermList.isEmpty()
                _searchHistory.value = response.recentSearchTermList
            } catch (e: Exception) {
                Log.e(TAG, e.message.toString())
            } finally {
                isLoading = false
            }
        }
    }


    /**
     * 검색 기록 저장 설정
     */
    fun setIsHistoryActivated() {
        if (isLoading) return

        viewModelScope.launch {
            try {
                val response = searchService.toggleAutoSave().unwrapOrThrow()

                prefs.edit() { putBoolean(PREF_KEY_SEARCH_HISTORY_ACTIVATED, response.isAutoSaved) }
                _isHistoryActivated.value = response.isAutoSaved
            } catch (e: Exception) {
                Log.e(TAG, e.message.toString())
            }
        }
    }


    /**
     * 모든 검색 기록 삭제
     */
    fun removeAllSearchHistory() {
        if (isLoading) return

        viewModelScope.launch {
            isLoading = true

            try {
                searchService.deleteAllSearchHistory().unwrapOrThrow()
                _isSearchHistoryEmpty.value = true
                _searchHistory.value = emptyList()
            } catch (e: Exception) {
                Log.e(TAG, e.message.toString())
            } finally {
                isLoading = false
            }
        }
    }


    /**
     * 추천 검색어 로드
     */
    private fun loadRecommendedKeyword() {
        viewModelScope.launch {
            isLoading = true

            try {
                val response = searchService.getRecommendedKeyword().unwrapOrThrow()
                _recommendedKeyword.value = response.recommendKeywords
            } catch (e: Exception) {
                Log.e(TAG, e.message.toString())
            } finally {
                isLoading = false
            }
        }
    }

    /**
     * 검색 창 닫기
     */
    fun clearSearch() {
        _booksSearchResult.value = emptyList()
        _articlesSearchResult.value = emptyList()
        _booksHasNextPage.value = false
        _articlesHasNextPage.value = false
    }
}