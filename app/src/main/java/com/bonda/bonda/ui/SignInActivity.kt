package com.bonda.bonda.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import com.bonda.bonda.ui.home.HomeActivity
import com.bonda.bonda.util.AccessTokenProvider
import com.bonda.bonda.util.PREFS_NAME
import com.bonda.bonda.util.PREF_KEY_REFRESH_TOKEN
import com.bonda.bonda.util.TAG
import com.kakao.sdk.user.UserApiClient
import kotlinx.coroutines.launch
import androidx.core.content.edit
import com.bonda.bonda.databinding.ActivitySignInBinding
import com.bonda.bonda.network.ApiClient
import com.bonda.bonda.network.model.auth.LoginRequest
import com.bonda.bonda.util.PREF_KEY_SIGNUP_REQUIRED

class SignInActivity : AppCompatActivity() {

    private val authService = ApiClient.authService
    private lateinit var binding: ActivitySignInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.signInButton.setOnClickListener {
            var accessToken: String
            var refreshToken: String
            var isNewUser: Boolean

            if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
                UserApiClient.instance.loginWithKakaoTalk(this) { token, error ->
                    if (error != null) {
                        /**
                         * 일반적으로 카카오톡을 이용해서 로그인 하다가 유저가 취소 버튼을 누른 경우
                         */
                        Log.e("DEBUG", "kakao talk 로그인 실패", error)
                    } else if (token != null) {
                        Log.i("DEBUG", "kakao talk 로그인 성공 ${token.idToken}")

                        lifecycleScope.launch {
                            try {
                                val response = authService.login(LoginRequest(token.idToken!!))
                                    .unwrapOrThrow()

                                Log.d("DEBUG", response.accessToken)
                                Log.d("DEBUG", response.refreshToken)
                                Log.d("DEBUG", response.isNewUser.toString())

                                accessToken = response.accessToken
                                refreshToken = response.refreshToken
                                isNewUser = response.isNewUser

                                AccessTokenProvider.setAccessToken(accessToken)
                                getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit() {
                                    putString(PREF_KEY_REFRESH_TOKEN, refreshToken)
                                    putBoolean(PREF_KEY_SIGNUP_REQUIRED, isNewUser)
                                }

                                if (isNewUser) {
                                    startActivity(
                                        Intent(
                                            this@SignInActivity,
                                            SignUpActivity::class.java
                                        )
                                    )

                                    Log.d(TAG, "bonda 회원 가입이 필요합니다")

                                    finish()
                                } else {
                                    startActivity(
                                        Intent(
                                            this@SignInActivity,
                                            HomeActivity::class.java
                                        )
                                    )

                                    Log.d(TAG, "bonda 로그인 완료")

                                    finish()
                                }
                            } catch (e: Exception) {
                                Log.e("DEBUG", e.toString())
                            }
                        }
                    }
                }

            } else {
                UserApiClient.instance.loginWithKakaoAccount(this) { token, error ->
                    if (error != null) {
                        /**
                         * 일반적으로 카카오 계정을 이용해서 로그인 하다가 유저가 취소 버튼을 누른 경우
                         */
                        Log.e("DEBUG", "kakao account 로그인 실패", error)
                    } else if (token != null) {
                        Log.i("DEBUG", "kakao account 로그인 성공 ${token.idToken}")

                        lifecycleScope.launch {
                            try {
                                val response = authService.login(LoginRequest(token.idToken!!))
                                    .unwrapOrThrow()

                                Log.d("DEBUG", response.accessToken)
                                Log.d("DEBUG", response.refreshToken)
                                Log.d("DEBUG", response.isNewUser.toString())

                                accessToken = response.accessToken
                                refreshToken = response.refreshToken
                                isNewUser = response.isNewUser

                                AccessTokenProvider.setAccessToken(accessToken)
                                getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit() {
                                    putString(PREF_KEY_REFRESH_TOKEN, refreshToken)
                                    putBoolean(PREF_KEY_SIGNUP_REQUIRED, isNewUser)
                                }

                                if (isNewUser) {
                                    startActivity(
                                        Intent(
                                            this@SignInActivity,
                                            SignUpActivity::class.java
                                        )
                                    )

                                    Log.d(TAG, "bonda 회원 가입이 필요합니다")

                                    finish()
                                } else {
                                    startActivity(
                                        Intent(
                                            this@SignInActivity,
                                            HomeActivity::class.java
                                        )
                                    )

                                    Log.d(TAG, "bonda 로그인 완료")

                                    finish()
                                }
                            } catch (e: Exception) {
                                Log.e("DEBUG", e.toString())
                            }
                        }
                    }
                }
            }
        }
    }
}