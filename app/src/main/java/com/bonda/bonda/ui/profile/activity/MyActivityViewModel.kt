package com.bonda.bonda.ui.profile.activity

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bonda.bonda.network.ApiClient
import com.bonda.bonda.network.model.member.GetCollectedBadgesResponse
import com.bonda.bonda.network.model.member.GetMyActivityResponse
import com.bonda.bonda.model.TAG
import kotlinx.coroutines.launch

class MyActivityViewModel : ViewModel() {
    /**
     * 네트워크 인스턴스 로드
     */
    private val memberService = ApiClient.memberService

    /**
     * 라이브 데이터
     */
    private val _isLoading = MutableLiveData(true)
    private val _isError = MutableLiveData(false)
    private val _viewedBookCount = MutableLiveData<Int>()
    private val _collectedBookCount = MutableLiveData<Int>()
    private val _collectedBadgeCount = MutableLiveData<Int>()
    private val _collectedBookCategory = MutableLiveData<List<GetMyActivityResponse.Category>>()
    private val _collectedBadgeList =
        MutableLiveData<List<GetCollectedBadgesResponse.Badge>>(emptyList())

    /**
     * 읽기 전용 데이터
     */
    val isLoading: MutableLiveData<Boolean>
        get() = _isLoading
    val isError: MutableLiveData<Boolean>
        get() = _isError
    val viewedBookCount: MutableLiveData<Int>
        get() = _viewedBookCount
    val collectedBookCount: MutableLiveData<Int>
        get() = _collectedBookCount
    val collectedBadgeCount: MutableLiveData<Int>
        get() = _collectedBadgeCount
    val collectedBookCategory: MutableLiveData<List<GetMyActivityResponse.Category>>
        get() = _collectedBookCategory
    val collectedBadgeList: MutableLiveData<List<GetCollectedBadgesResponse.Badge>>
        get() = _collectedBadgeList

    init {
        getMyActivity()
    }

    fun getMyActivity() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _isError.value = false

                val bookRes = memberService.getMyActivity().unwrapOrThrow()
                val badgeRes = memberService.getCollectedBadges().unwrapOrThrow()

                /**
                 * 탐색한 도서와 수집한 도서 갯수 저장
                 */
                _viewedBookCount.value = bookRes.bookViewCount
                _collectedBookCount.value = bookRes.bookcaseCount

                /**
                 * 카테고리별 도서 갯수 기준 내림차순 정렬 후 저장
                 */
                _collectedBookCategory.value =
                    bookRes.categoryCountList.sortedByDescending { it.count }

                /**
                 * 뱃지 갯수와 리스트 저장
                 */
                _collectedBadgeCount.value = badgeRes.badgeCount
                _collectedBadgeList.value = (badgeRes.viewBadgeList + badgeRes.saveBadgeList)

            } catch (e: Exception) {
                _isError.value = true
                Log.e(TAG, e.toString())
            } finally {
                _isLoading.value = false
            }
        }
    }

}