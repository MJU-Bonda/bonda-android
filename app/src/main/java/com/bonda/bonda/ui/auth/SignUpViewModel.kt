package com.bonda.bonda.ui.auth

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SignUpViewModel : ViewModel() {
    /**
     * 사용자 닉네임 live data 선언
     */
    private val _username = MutableLiveData("")
    val username: LiveData<String> = _username

    /**
     * 입력한 사용자 이름은 trim해서 저장합니다
     */
    fun setUsername(input: String) {
        val cleanedInput = input.trim()
        _username.value = if (cleanedInput.startsWith("|KAKAO")) "" else cleanedInput
    }

}
