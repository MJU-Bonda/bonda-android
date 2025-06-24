package com.bonda.bonda

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bonda.bonda.ui.home.HomeActivity
import com.bonda.bonda.ui.SignInActivity
import com.bonda.bonda.util.AccessTokenProvider
import com.bonda.bonda.util.PREFS_NAME
import com.bonda.bonda.util.PREF_KEY_REFRESH_TOKEN
import com.bonda.bonda.util.TAG
import kotlinx.coroutines.launch
import androidx.core.content.edit
import com.bonda.bonda.network.ApiClient
import com.bonda.bonda.network.model.auth.ReissueRequest
import com.bonda.bonda.ui.PermissionRequestActivity
import com.bonda.bonda.ui.ProfileSetupActivity
import com.bonda.bonda.util.PREF_KEY_PERMISSION_REQUIRED
import com.bonda.bonda.util.PREF_KEY_SIGNUP_REQUIRED

class MainActivity : AppCompatActivity() {

    private val authService = ApiClient.authService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        // 앱 권한 검사
        if (prefs.getBoolean(PREF_KEY_PERMISSION_REQUIRED, true)) {
            startActivity(
                Intent(this, PermissionRequestActivity::class.java)
            )

            finish()
        } else {
            val refreshToken = prefs.getString(PREF_KEY_REFRESH_TOKEN, null)

            if (refreshToken == null) {
                Log.d(TAG, "로그인이 필요한 서비스입니다")

                startActivity(
                    Intent(this, SignInActivity::class.java)
                )

                finish()
            } else {
                lifecycleScope.launch {
                    try {
                        val response = authService.reissueAccessToken(ReissueRequest(refreshToken))
                            .unwrapOrThrow()

                        Log.d(TAG, "access token reissue 완료")

                        val accessToken = response.accessToken
                        AccessTokenProvider.setAccessToken(accessToken)

                        Log.d(TAG, "new token: $accessToken")

                        val signupRequired = prefs.getBoolean(PREF_KEY_SIGNUP_REQUIRED, false)

                        if (signupRequired) {
                            startActivity(
                                Intent(this@MainActivity, ProfileSetupActivity::class.java)
                            )

                            Log.d(TAG, "회원가입이 필요한 서비스입니다")

                            finish()
                        } else {
                            startActivity(
                                Intent(this@MainActivity, HomeActivity::class.java)
                            )

                            Log.d(TAG, "로그인 완료")

                            finish()
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "access token reissue 중 문제가 발생했습니다")

                        prefs.edit() {
                            remove(PREF_KEY_REFRESH_TOKEN)
                            remove(PREF_KEY_SIGNUP_REQUIRED)
                        }

                        startActivity(
                            Intent(this@MainActivity, SignInActivity::class.java)
                        )

                        Log.d(TAG, "로그아웃 됨")

                        finish()
                    }
                }
            }
        }
    }
}
