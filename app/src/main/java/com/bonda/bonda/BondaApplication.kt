package com.bonda.bonda

import android.app.Application
import com.bonda.bonda.network.ApiClient
import com.bonda.bonda.util.AccessTokenProvider
import com.kakao.sdk.common.KakaoSdk

class BondaApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        KakaoSdk.init(this, BuildConfig.kakaoAppKey)
        ApiClient.init(this, AccessTokenProvider)
    }
}
