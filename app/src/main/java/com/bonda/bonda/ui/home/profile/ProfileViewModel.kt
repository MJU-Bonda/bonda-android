package com.bonda.bonda.ui.home.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bonda.bonda.network.ApiClient
import com.bonda.bonda.util.TAG
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {
    /**
     * 서비스 인스턴스 생성
     */
    private val memberService = ApiClient.memberService

    /**
     * live-data declaration
     */
    private val _username = MutableLiveData<String>()
    private val _profileImage = MutableLiveData("")
    private val _savedBookCount = MutableLiveData<Int>()
    private val _collectedBadgeCount = MutableLiveData<Int>()

    /**
     * read-only properties
     */
    val username: LiveData<String> = _username
    val profileImage: LiveData<String> = _profileImage
    val savedBookCount: LiveData<Int> = _savedBookCount
    val collectedBadgeCount: LiveData<Int> = _collectedBadgeCount

    /**
     * init
     */
    init {
        viewModelScope.launch {
            try {
                val res = memberService.getProfile().unwrapOrThrow()

                _username.value = res.nickname
                if (!res.profileImage.isNullOrBlank())
                    _profileImage.value = res.profileImage.toString()
                _savedBookCount.value = res.savedBookCount
                _collectedBadgeCount.value = res.badgeCount

                Log.d(TAG, res.toString())
            } catch (e: Exception) {
                Log.e(TAG, e.message.toString())
            }
        }
    }
}