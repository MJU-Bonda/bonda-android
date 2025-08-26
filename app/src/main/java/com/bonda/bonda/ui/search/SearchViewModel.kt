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
import com.bonda.bonda.model.SortOrder

class SearchViewModel(application: Application) : AndroidViewModel(application) {

    private val searchService = ApiClient.searchService
    private var bookPage = 0
    private var articlePage = 0
    var searchKeyword = ""
        private set

    private val prefs = application.getSharedPreferences(PREFS_NAME, MODE_PRIVATE)

    private var isLoading = false
    private val _isSearchHistoryEmpty = MutableLiveData<Boolean>()
    private val _isHistoryActivated = MutableLiveData<Boolean>()
    private val _searchHistory = MutableLiveData<List<String>>()
    private val _recommendedKeyword = MutableLiveData<List<String>>()
    private val _booksSearchResult = MutableLiveData<List<Book>>(emptyList())
    private val _articlesSearchResult = MutableLiveData<List<Article>>(emptyList())
    private val _booksHasNextPage = MutableLiveData(false)
    private val _articlesHasNextPage = MutableLiveData(false)
    private val _booksSearchResultCount = MutableLiveData(0)
    private val _articlesSearchResultCount = MutableLiveData(0)

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

    private val _sortOrder = MutableLiveData(SortOrder.NEWEST)
    val sortOrder: LiveData<SortOrder> = _sortOrder

    init {
        _isHistoryActivated.value = prefs.getBoolean(PREF_KEY_SEARCH_HISTORY_ACTIVATED, false)
        loadSearchHistory()
        loadRecommendedKeyword()
    }

    /**
     * 현재 검색 결과를 초기화하고 새로운 검색을 수행합니다
     * @param keyword 검색어
     * @param clearPreviousResults 이전 검색 결과를 지울지 여부
     */
    fun search(keyword: String, clearPreviousResults: Boolean = true) {
        if (clearPreviousResults) {
            clearSearch()
        }
        bookPage = 0
        articlePage = 0

        searchKeyword = keyword
        searchBooks(keyword)
        searchArticles(keyword)

        if (clearPreviousResults) {
            loadSearchHistory()
        }
    }

    /**
     * 정렬 기준을 토글한 뒤, 검색을 다시 수행합니다. 검색어가 없으면 실행되지 않습니다.
     */
    fun toggleSortOrderAndSearch() {
        if (searchKeyword.isBlank()) return

        val newSortOrder = if (_sortOrder.value == SortOrder.NEWEST) {
            SortOrder.POPULARITY
        } else {
            SortOrder.NEWEST
        }
        _sortOrder.value = newSortOrder

        search(searchKeyword, clearPreviousResults = false)
    }

    /**
     * 도서 검색 결과 로드
     */
    private fun searchBooks(keyword: String) {
        viewModelScope.launch {
            try {
                val res = searchService
                    .searchBooks(
                        page = bookPage,
                        word = keyword,
                        orderBy = _sortOrder.value?.code ?: SortOrder.NEWEST.code
                    )
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

                val updated = if (bookPage == 0) newBooks else current + newBooks

                _booksSearchResult.value = updated
                _booksHasNextPage.value = res.hasNextPage
                _booksSearchResultCount.value = res.total
            } catch (e: Exception) {
                Log.e(TAG, "SearchViewModel.kt::searchBooks()", e)
            }
        }
    }

    /**
     * 도서 검색 결과 다음 페이지가 있으면 로드
     */
    fun getNextBooks() {
        if (_booksHasNextPage.value != true) return
        bookPage++
        searchBooks(searchKeyword)
    }

    /**
     * 아티클 검색
     */
    private fun searchArticles(keyword: String) {
        viewModelScope.launch {
            try {
                val res = searchService
                    .searchArticles(
                        page = articlePage,
                        word = keyword,
                        orderBy = _sortOrder.value?.code ?: SortOrder.NEWEST.code
                    )
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
            } catch (e: Exception) {
                Log.e(TAG, "SearchViewModel.kt::searchArticles()", e)
            }
        }
    }

    /**
     * 아티클 다음 페이지가 있으면 로드
     */
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
                Log.e(TAG, "SearchViewModel.kt::removeSearchHistory()", e)
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
                Log.e(TAG, "SearchViewModel.kt::loadSearchHistory()", e)
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
                Log.e(TAG, "SearchViewModel.kt::setIsHistoryActivated()", e)
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
                Log.e(TAG, "SearchViewModel.kt::removeAllSearchHistory()", e)
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
                Log.e(TAG, "SearchViewModel.kt::loadRecommendedKeyword()", e)
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
        _booksSearchResultCount.value = 0
        _articlesSearchResultCount.value = 0
    }

}