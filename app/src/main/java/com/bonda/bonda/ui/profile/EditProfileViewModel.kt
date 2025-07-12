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

    private val memberService = ApiClient.memberService

    private val _profileImage = MutableLiveData<String>()

    val profileImage: LiveData<String> = _profileImage

    init {
        viewModelScope.launch {
            try {
                val res = memberService.getProfile().unwrapOrThrow()

                _profileImage.value = res.profileImage

                Log.d(TAG, res.toString())
            } catch (e: Exception) {
                Log.e(TAG, e.message.toString())
            }
        }
    }
}