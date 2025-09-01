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
    /**
     * 네트워크 인스턴스를 생성합니다
     */
    private val searchService = ApiClient.searchService

    /**
     * 페이지네이션용 내부 변수를 설정합니다
     */
    private var bookPage = 0
    private var articlePage = 0
    var searchKeyword = ""
        private set

    /**
     * 검색기록 저장 여부를 로컬에서 불러옵니다
     */
    private val prefs = application.getSharedPreferences(PREFS_NAME, MODE_PRIVATE)

    /**
     * 라이브 데이터 선언
     */
    private var _isLoading = MutableLiveData(false)
    private val _isError = MutableLiveData(false)
    private val _sortOrder = MutableLiveData(SortOrder.NEWEST)
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

    /**
     * 읽기 전용 변수 선언
     */
    val isLoading: LiveData<Boolean> = _isLoading
    val isError: LiveData<Boolean> = _isError
    val sortOrder: LiveData<SortOrder> = _sortOrder
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
     * 검색을 수행합니다
     */
    fun search(keyword: String, clearPreviousResults: Boolean = true) {
        if (_isLoading.value == true) return

        viewModelScope.launch {
            if (clearPreviousResults) {
                clearSearch()
            }
            bookPage = 0
            articlePage = 0
            searchKeyword = keyword

            _isLoading.value = true
            _isError.value = false

            try {
                val bookJob = launch { searchBooksInternal(keyword) }
                val articleJob = launch { searchArticlesInternal(keyword) }
                bookJob.join()
                articleJob.join()
            } catch (e: Exception) {
                Log.e(TAG, "SearchViewModel.kt::search()", e)
                _isError.value = true
            } finally {
                _isLoading.value = false
            }

            if (clearPreviousResults) {
                loadSearchHistory()
            }
        }
    }

    /**
     * 검색 정렬 순서를 변경합니다
     */
    fun toggleSortOrderAndSearch() {
        if (searchKeyword.isBlank()) return

        val newSortOrder = if (_sortOrder.value == SortOrder.NEWEST) {
            SortOrder.POPULARITY
        } else {
            SortOrder.NEWEST
        }
        _sortOrder.value = newSortOrder

        search(searchKeyword, clearPreviousResults = true)
    }

    /**
     * 도서 검색 결과를 로드합니다
     */
    private suspend fun searchBooksInternal(keyword: String) {
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

            _booksSearchResult.postValue(updated)
            _booksHasNextPage.postValue(res.hasNextPage)
            _booksSearchResultCount.postValue(res.total)
        } catch (e: Exception) {
            Log.e(TAG, "SearchViewModel.kt::searchBooksInternal()", e)
            _isError.postValue(true)
        }
    }

    /**
     * 도서 검색 결과 다음 페이지가 존재하면 다음 페이지를 로드합니다
     */
    fun getNextBooks() {
        if (_booksHasNextPage.value != true || _isLoading.value == true) return
        bookPage++
        viewModelScope.launch {
            _isLoading.value = true
            try {
                searchBooksInternal(searchKeyword)
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * 아티클 검색 결과를 로드합니다
     */
    private suspend fun searchArticlesInternal(keyword: String) {
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

            val updated = if (articlePage == 0) newArticles else current + newArticles

            _articlesSearchResult.postValue(updated)
            _articlesHasNextPage.postValue(res.hasNextPage)
            _articlesSearchResultCount.postValue(res.total)
        } catch (e: Exception) {
            Log.e(TAG, "SearchViewModel.kt::searchArticlesInternal()", e)
            _isError.postValue(true)
        }
    }

    /**
     * 뉴스 검색 결과 다음 페이지가 존재하면 다음 페이지를 로드합니다
     */
    fun getNextArticles() {
        if (_articlesHasNextPage.value != true || _isLoading.value == true) return
        articlePage++
        viewModelScope.launch {
            _isLoading.value = true
            try {
                searchArticlesInternal(searchKeyword)
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * 검색 기록을 삭제합니다
     */
    fun removeSearchHistory(keyword: String) {
        if (_isLoading.value == true) return

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
     * 검색 기록을 로드합니다
     */
    private fun loadSearchHistory() {
        viewModelScope.launch {
            try {
                _isLoading.value = true

                val response = searchService.getSearchHistory().unwrapOrThrow()

                _isSearchHistoryEmpty.value = response.recentSearchTermList.isEmpty()
                _searchHistory.value = response.recentSearchTermList
            } catch (e: Exception) {
                Log.e(TAG, "SearchViewModel.kt::loadSearchHistory()", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * 검색 기록 활성화 여부를 요청합니다
     */
    fun setIsHistoryActivated() {
        if (_isLoading.value == true) return

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
     * 검색 기록 전체 삭제를 요청합니다
     */
    fun removeAllSearchHistory() {
        if (_isLoading.value == true) return

        viewModelScope.launch {
            _isLoading.value = true

            try {
                searchService.deleteAllSearchHistory().unwrapOrThrow()
                _isSearchHistoryEmpty.value = true
                _searchHistory.value = emptyList()
            } catch (e: Exception) {
                Log.e(TAG, "SearchViewModel.kt::removeAllSearchHistory()", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * 오늘의 추천 키워드를 로드합니다
     */
    private fun loadRecommendedKeyword() {
        viewModelScope.launch {
            _isLoading.value = true

            try {
                val response = searchService.getRecommendedKeyword().unwrapOrThrow()
                _recommendedKeyword.value = response.recommendKeywords
            } catch (e: Exception) {
                Log.e(TAG, "SearchViewModel.kt::loadRecommendedKeyword()", e)
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * 검색창을 닫습니다
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