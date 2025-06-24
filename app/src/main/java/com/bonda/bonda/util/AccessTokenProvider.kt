package com.bonda.bonda.util

object AccessTokenProvider {

    @Volatile
    private var _accessToken: String? = null

    fun setAccessToken(token: String) {
        _accessToken = token
    }

    fun getAccessToken(): String? = _accessToken

    fun removeAccessToken() {
        _accessToken = null
    }

}
