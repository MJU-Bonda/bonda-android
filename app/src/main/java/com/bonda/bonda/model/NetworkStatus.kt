package com.bonda.bonda.model

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities

object NetworkStatus {
    /**
     * 네트워크가 정상적으로 연결되어있는지 검사하고 네트워크가 연결되어있으면 ture를 반환합니다
     */
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val network = connectivityManager.activeNetwork ?: return false
        val capabilities =
            connectivityManager.getNetworkCapabilities(network) ?: return false

        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}