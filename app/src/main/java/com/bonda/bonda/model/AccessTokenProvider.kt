package com.bonda.bonda.model

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
