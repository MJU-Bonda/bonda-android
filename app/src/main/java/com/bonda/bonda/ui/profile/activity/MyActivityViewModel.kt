package com.bonda.bonda.ui.profile.activity

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bonda.bonda.network.ApiClient
import com.bonda.bonda.network.model.member.GetCollectedBadgesResponse
import com.bonda.bonda.network.model.member.GetMyActivityResponse
import com.bonda.bonda.util.TAG
import kotlinx.coroutines.launch

class MyActivityViewModel : ViewModel() {

    private val memberService = ApiClient.memberService

    private val _viewedBookCount = MutableLiveData<Int>()
    private val _collectedBookCount = MutableLiveData<Int>()
    private val _collectedBadgeCount = MutableLiveData<Int>()
    private val _collectedBookCategory = MutableLiveData<List<GetMyActivityResponse.Category>>()
    private val _collectedBadgeList =
        MutableLiveData<List<GetCollectedBadgesResponse.Badge>>(emptyList())

    val viewedBookCount: MutableLiveData<Int>
        get() = _viewedBookCount
    val collectedBookCount: MutableLiveData<Int>
        get() = _collectedBookCount
    val collectedBookCategory: MutableLiveData<List<GetMyActivityResponse.Category>>
        get() = _collectedBookCategory
    val collectedBadgeCount: MutableLiveData<Int>
        get() = _collectedBadgeCount
    val collectedBadgeList: MutableLiveData<List<GetCollectedBadgesResponse.Badge>>
        get() = _collectedBadgeList

    init {
        viewModelScope.launch {
            try {
                val bookRes = memberService.getMyActivity().unwrapOrThrow()
                val badgeRes = memberService.getCollectedBadges().unwrapOrThrow()

                /**
                 * 탐색한 도서와 수집한 도서 갯수 저장
                 */
                _viewedBookCount.value = bookRes.bookViewCount
                _collectedBookCount.value = bookRes.bookcaseCount

                /**
                 * 카테고리별 도서 갯수 정렬 후 저장
                 */
                _collectedBookCategory.value = bookRes.categoryCountList
                    .sortedByDescending { it.count }
                    .let { sorted ->
                        if (sorted.size <= 3) {
                            sorted
                        } else {
                            val top3 = sorted.take(3)
                            val etcSum = sorted.drop(3).sumOf { it.count }
                            top3 + GetMyActivityResponse.Category(
                                category = "기타",
                                count = etcSum
                            )
                        }
                    }

                /**
                 * 뱃지 갯수와 리스트 저장
                 */
                _collectedBadgeCount.value = badgeRes.badgeCount
                _collectedBadgeList.value = (badgeRes.viewBadgeList + badgeRes.saveBadgeList)

            } catch (e: Exception) {
                Log.e(TAG, e.toString())
            }
        }
    }

}