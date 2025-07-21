package com.bonda.bonda.ui.profile.activity

import android.graphics.Color
import android.util.Log
import androidx.core.graphics.toColor
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bonda.bonda.R
import com.bonda.bonda.network.ApiClient
import com.bonda.bonda.network.model.member.GetCollectedBadgesResponse
import com.bonda.bonda.util.TAG
import kotlinx.coroutines.launch

class MyActivityViewModel : ViewModel() {

    private val memberService = ApiClient.memberService

    private val _viewedBookCount = MutableLiveData<Int>()
    private val _collectedBookCount = MutableLiveData<Int>()
    private val _collectedBadgeCount = MutableLiveData<Int>()
    private val _collectedBookCategory = MutableLiveData<List<BookCategory>>()
    private val _collectedBadgeList =
        MutableLiveData<List<GetCollectedBadgesResponse.Badge>>(emptyList())

    val viewedBookCount: MutableLiveData<Int>
        get() = _viewedBookCount
    val collectedBookCount: MutableLiveData<Int>
        get() = _collectedBookCount
    val collectedBookCategory: MutableLiveData<List<BookCategory>>
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

                _viewedBookCount.value = bookRes.bookViewCount
                _collectedBookCount.value = bookRes.bookcaseCount

                // TODO 컬러 맵핑 코드 수정
                bookRes.categoryCountList.map {
                    BookCategory(it.category, it.count, R.color.surface_default_base.toColor())
                }

                _collectedBadgeCount.value = badgeRes.badgeCount
                _collectedBadgeList.value = (badgeRes.viewBadgeList + badgeRes.saveBadgeList)
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
            }
        }
    }

    data class BookCategory(
        val name: String,
        val count: Int,
        val color: Color
    )

    val categories = listOf(
        BookCategory("사진집", 10, R.color.surface_graph_tertiary.toColor()),
        BookCategory("시집", 8, R.color.surface_graph_primary.toColor()),
        BookCategory("만화/그래픽노블", 5, R.color.surface_graph_secondary.toColor()),
        BookCategory("기타", 4, R.color.surface_default_base.toColor())
    )
}