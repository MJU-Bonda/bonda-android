package com.bonda.bonda.ui.profile

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bonda.bonda.network.ApiClient
import com.bonda.bonda.util.TAG
import kotlinx.coroutines.launch

class EditProfileViewModel : ViewModel() {
    /**
     * 네트워크 서비스 선언
     */
    private val memberService = ApiClient.memberService

    /**
     * 사용자 프로필 이미지와 닉네임 live data 선언
     */
    private val _profileImage = MutableLiveData("")
    private val _currentUsername = MutableLiveData("")
    private val _newUsername = MutableLiveData("")

    val profileImage: LiveData<String> = _profileImage
    val currentUsername: LiveData<String> = _currentUsername
    val newUsername: LiveData<String> = _newUsername

    /**
     * 사용자 정보 로드
     */
    init {
        viewModelScope.launch {
            try {
                val res = memberService.getProfile().unwrapOrThrow()

                if (!res.profileImage.isNullOrBlank())
                    _profileImage.value = res.profileImage
                _currentUsername.value = res.nickname

            } catch (e: Exception) {
                Log.e(TAG, e.message.toString())
            }
        }
    }

    /**
     * 입력한 사용자 이름을 trim해서 저장합니다
     */
    fun setUsername(input: String) {
        val cleanedInput = input.trim()
        _newUsername.value = if (cleanedInput.startsWith("|KAKAO")) "" else cleanedInput
    }

}
