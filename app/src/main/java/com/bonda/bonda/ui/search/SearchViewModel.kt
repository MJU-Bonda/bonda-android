package com.bonda.bonda.ui.search

import android.app.Application
import android.util.Log
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.bonda.bonda.network.ApiClient
import com.bonda.bonda.util.PREFS_NAME
import com.bonda.bonda.util.PREF_KEY_SEARCH_HISTORY_ACTIVATED
import com.bonda.bonda.util.TAG
import kotlinx.coroutines.launch
import androidx.core.content.edit

class SearchViewModel(application: Application) : AndroidViewModel(application) {

    private val searchService = ApiClient.searchService

    private val prefs = application.getSharedPreferences(PREFS_NAME, MODE_PRIVATE)

    private val _isLoading = MutableLiveData<Boolean>()
    private val _isEmpty = MutableLiveData<Boolean>()
    private val _isHistoryActivated = MutableLiveData<Boolean>()
    private val _searchText = MutableLiveData<String>()
    private val _searchHistory = MutableLiveData<List<String>>()
    private val _recommendedKeyword = MutableLiveData<List<String>>()
    private val _bookSearchResult = MutableLiveData<List<Book>>()
    private val _articleSearchResult = MutableLiveData<List<Article>>()

    val isLoading: LiveData<Boolean> = _isLoading
    val isEmpty: LiveData<Boolean> = _isEmpty
    val isHistoryActivated: LiveData<Boolean> = _isHistoryActivated
    val searchText: LiveData<String> = _searchText
    val searchHistory: LiveData<List<String>> = _searchHistory
    val recommendedKeyword: LiveData<List<String>> = _recommendedKeyword
    val bookSearchResult: LiveData<List<Book>> = _bookSearchResult
    val articleSearchResult: LiveData<List<Article>> = _articleSearchResult

    data class Book(
        val id: Int,
        val title: String,
        val author: String,
        val imageUrl: String,
        val category: String
    )

    data class Article(
        val id: Int,
        val title: String,
        val imageUrl: String,
        val category: String
    )

    init {
//        _bookSearchResult.value = emptyList()
//        _articleSearchResult.value = emptyList()
        _isHistoryActivated.value = prefs.getBoolean(PREF_KEY_SEARCH_HISTORY_ACTIVATED, false)

        loadSearchHistory()
        loadRecommendedKeyword()
    }


    fun searchBooks(keyword: String) {

    }

    fun searchArticles(keyword: String) {

    }


    fun setSearchText(text: String) {
        _searchText.value = text
    }


    /**
     * 특정 검색어 삭제
     */
    fun removeSearchHistory(keyword: String) {
        if (_isLoading.value == true) return

        viewModelScope.launch {
            try {
                searchService.deleteSearchHistory(keyword)

                _searchHistory.value = _searchHistory.value
                    ?.filterNot { it == keyword }
                _isEmpty.value = _searchHistory.value!!.isEmpty()
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
                _isLoading.value = true

                val response = searchService.getSearchHistory().unwrapOrThrow()

                _isEmpty.value = response.recentSearchTermList.isEmpty()
                _searchHistory.value = response.recentSearchTermList
            } catch (e: Exception) {
                Log.e(TAG, e.message.toString())
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * 검색 기록 저장 설정
     */
    fun setIsHistoryActivated() {
        if (_isLoading.value == true) return

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
        if (_isLoading.value == true) return

        viewModelScope.launch {
            _isLoading.value = true

            try {
                searchService.deleteAllSearchHistory().unwrapOrThrow()
                _isEmpty.value = true
                _searchHistory.value = emptyList()
            } catch (e: Exception) {
                Log.e(TAG, e.message.toString())
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * 추천 검색어 로드
     */
    private fun loadRecommendedKeyword() {
        viewModelScope.launch {
            _isLoading.value = true

            try {
                val response = searchService.getRecommendedKeyword().unwrapOrThrow()
                _recommendedKeyword.value = response.recommendKeywords
            } catch (e: Exception) {
                Log.e(TAG, e.message.toString())
            } finally {
                _isLoading.value = false
            }
        }
    }
}