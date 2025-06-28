package com.bonda.bonda.ui.search

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bonda.bonda.network.ApiClient
import com.bonda.bonda.util.AccessTokenProvider
import com.bonda.bonda.util.TAG
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {

    private val searchService = ApiClient.searchService

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
        _recommendedKeyword.value = listOf(
            "여름",
            "에세이",
            "제주"
        )

//        _bookSearchResult.value = emptyList()
//        _articleSearchResult.value = emptyList()

        getIsHistoryActivated()
        loadSearchHistory()
        loadRecommendedKeyword()
    }

    fun setSearchText(text: String) {
        _searchText.value = text
    }

    private fun getIsHistoryActivated() {
        viewModelScope.launch {
            try{
                _isLoading.value = true

                // TODO history 저장 여부 api 연결

                _isHistoryActivated.value = true
            } catch (e: Exception){
                Log.e(TAG, e.toString())
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun setIsHistoryActivated() {
        if (_isLoading.value == true) return

        viewModelScope.launch {
            try {
                // TODO history 저장 설정 api 연결

                _isHistoryActivated.value = !(_isHistoryActivated.value ?: false)
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
            }
        }
    }

    private fun loadSearchHistory() {
        viewModelScope.launch {
            try {
                _isLoading.value = true

                val response = searchService.getSearchHistory(
                    AccessTokenProvider.getAccessToken().toString()
                ).unwrapOrThrow()

                _isEmpty.value = response.recentSearchTermList.isEmpty()
                _searchHistory.value = response.recentSearchTermList
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun removeSearchHistory(keyword: String) {
        if (_isLoading.value == true) return

        try {
            // TODO 삭제 api 연결

            _searchHistory.value = _searchHistory.value
                ?.filterNot { it == keyword }
            _isEmpty.value = _searchHistory.value!!.isEmpty()

        } catch (e: Exception) {
            Log.e(TAG, e.toString())
        }
    }

    fun removeAllSearchHistory() {
        if (_isLoading.value == true) return

        try {
            // TODO 전체 삭제 api 연결

            _searchHistory.value = emptyList()
            _isEmpty.value = true
        } catch (e: Exception) {
            Log.e(TAG, e.toString())
        }
    }

    private fun loadRecommendedKeyword() {
        viewModelScope.launch {
            _isLoading.value = true

            // TODO 추천검색어 로드 api 연결
            try {

            } catch (e: Exception) {

            } finally {
                _isLoading.value = false
            }
        }
    }
}