package com.bonda.bonda

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bonda.bonda.ui.home.HomeActivity
import com.bonda.bonda.ui.auth.SignInActivity
import com.bonda.bonda.util.AccessTokenProvider
import com.bonda.bonda.util.PREFS_NAME
import com.bonda.bonda.util.PREF_KEY_REFRESH_TOKEN
import com.bonda.bonda.util.TAG
import kotlinx.coroutines.launch
import androidx.core.content.edit
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.bonda.bonda.network.ApiClient
import com.bonda.bonda.network.model.auth.ReissueRequest
import com.bonda.bonda.ui.auth.PermissionRequestActivity
import com.bonda.bonda.ui.auth.SignUpActivity
import com.bonda.bonda.util.PREF_KEY_PERMISSION_REQUIRED
import com.bonda.bonda.util.PREF_KEY_SIGNUP_REQUIRED

class MainActivity : AppCompatActivity() {

    private var initFinished = false
    private val authService = ApiClient.authService

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        splashScreen.setKeepOnScreenCondition { !initFinished }
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)

        /**
         * 최초 1회 실행 시에만 권한요청을 수행합니다
         */
        if (prefs.getBoolean(PREF_KEY_PERMISSION_REQUIRED, true)) {
            val intent = Intent(this, PermissionRequestActivity::class.java)
            startActivity(intent)

            initFinished = true
            finish()
        } else {
            val refreshToken = prefs.getString(PREF_KEY_REFRESH_TOKEN, null)

            if (refreshToken == null) {
                Log.d(TAG, "로그인이 필요한 서비스입니다")
                val intent = Intent(this, SignInActivity::class.java)
                startActivity(intent)

                initFinished = true
                finish()
            } else {
                lifecycleScope.launch {
                    try {
                        val response = authService.reissueAccessToken(ReissueRequest(refreshToken))
                            .unwrapOrThrow()

                        val accessToken = response.accessToken
                        AccessTokenProvider.setAccessToken(accessToken)

                        val signupRequired = prefs.getBoolean(PREF_KEY_SIGNUP_REQUIRED, false)

                        /**
                         * 회원가입이 필요하면 회원가입 페이지로 이동하고, 그렇지 않으면 메인 페이지로 이동합니다.
                         */
                        if (signupRequired) {
                            val intent = Intent(this@MainActivity, SignUpActivity::class.java)
                            startActivity(intent)

                            initFinished = true
                            finish()
                        } else {
                            val intent = Intent(this@MainActivity, HomeActivity::class.java)
                            startActivity(intent)

                            initFinished = true
                            finish()
                        }
                    } catch (e: Exception) {
                        prefs.edit() {
                            remove(PREF_KEY_REFRESH_TOKEN)
                            remove(PREF_KEY_SIGNUP_REQUIRED)
                        }

                        val intent = Intent(this@MainActivity, SignInActivity::class.java)
                        startActivity(intent)

                        initFinished = true
                        finish()
                    }
                }
            }
        }
    }

}
