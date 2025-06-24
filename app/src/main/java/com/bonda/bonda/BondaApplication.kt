package com.bonda.bonda

import android.app.Application
import com.bonda.bonda.network.ApiClient
import com.bonda.bonda.util.AccessTokenProvider
import com.kakao.sdk.common.KakaoSdk

class BondaApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        KakaoSdk.init(this, "52c614c7066242cd93789ae602f2c4f7")
        ApiClient.init(AccessTokenProvider)
    }
}
