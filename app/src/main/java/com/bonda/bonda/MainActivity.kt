package com.bonda.bonda

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bonda.bonda.ui.home.HomeActivity
import com.bonda.bonda.ui.auth.SignInActivity
import com.bonda.bonda.model.AccessTokenProvider
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
import com.bonda.bonda.ui.offline.OfflineActivity
import com.bonda.bonda.util.ERROR_CALLBACK_ACTIVITY
import com.bonda.bonda.util.NetworkUtils
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

            /**
             * 로그인이 되어있지 않은 경우
             */
            if (refreshToken == null) {
                Log.d(TAG, "로그인이 필요한 서비스입니다")
                val intent = Intent(this, SignInActivity::class.java)
                startActivity(intent)
                initFinished = true
                finish()

                /**
                 * 로그인이 되어있는 경우
                 */
            } else {
                /**
                 * 네트워크 연결이 되어있는경우
                 */
                if(NetworkUtils.isNetworkAvailable(this)) {
                    lifecycleScope.launch {
                        try {
                            /**
                             * accessToken 재발급
                             */
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
                            /**
                             * 문제 발생 시 로그아웃 후 로그인 페이지로 이동합니다
                             */
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
                    /**
                     * 네트워크 연결이 안되어있는 경우
                     */
                } else {
                    Log.e(TAG, "네트워크 연결이 필요한 서비스입니다")
                    val intent = Intent(this, OfflineActivity::class.java)
                    intent.putExtra(ERROR_CALLBACK_ACTIVITY, "main_activity")
                    startActivity(intent)
                    initFinished = true
                    finish()
                }
            }
        }
    }

}
