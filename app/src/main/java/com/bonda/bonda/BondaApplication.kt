package com.bonda.bonda

import android.app.Application
import com.bonda.bonda.network.ApiClient
import com.bonda.bonda.model.AccessTokenProvider
import com.kakao.sdk.common.KakaoSdk

class BondaApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        /**
         * kakao login sdk 인스턴스를 초기화합니다
         */
        KakaoSdk.init(this, BuildConfig.kakaoAppKey)

        /**
         * 네트워크 통신 모듈과 token provider를 초기화합니다
         */
        ApiClient.init(this, AccessTokenProvider)
    }

}
