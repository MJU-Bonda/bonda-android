package com.bonda.bonda.ui.signup

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import com.bonda.bonda.R
import com.bonda.bonda.network.LoginRequest
import com.bonda.bonda.network.RetrofitClient
import com.bonda.bonda.network.unwrap
import com.bonda.bonda.ui.home.HomeActivity
import com.bonda.bonda.util.AccessTokenProvider
import com.bonda.bonda.util.PREFS_NAME
import com.bonda.bonda.util.PREF_KEY_REFRESH_TOKEN
import com.bonda.bonda.util.TAG
import com.kakao.sdk.user.UserApiClient
import kotlinx.coroutines.launch
import androidx.core.content.edit
import com.bonda.bonda.databinding.ActivitySignInBinding
import com.bonda.bonda.util.PREF_KEY_SIGNUP_REQUIRED

class SignInActivity : AppCompatActivity() {

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
            var isNewUser: Boolean = false

            if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
                UserApiClient.instance.loginWithKakaoTalk(this) { token, error ->
                    if (error != null) {
                        Log.e("DEBUG", "로그인 실패", error)

                        Toast.makeText(
                            this,
                            getString(R.string.error_kakao_login), Toast.LENGTH_SHORT
                        ).show()

                    } else if (token != null) {
                        Log.i("DEBUG", "로그인 성공 ${token.idToken}")

                        lifecycleScope.launch {
                            try {
                                val response = RetrofitClient
                                    .retrofitService
                                    .login(LoginRequest(token.idToken!!))
                                    .unwrap()

                                Log.d("DEBUG", response.accessToken)
                                Log.d("DEBUG", response.refreshToken)
                                Log.d("DEBUG", response.isNewUser.toString())

                                accessToken = response.accessToken
                                refreshToken = response.refreshToken
                                isNewUser = response.isNewUser

                                AccessTokenProvider.setAccessToken(accessToken)
                                getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit() {
                                    putString(PREF_KEY_REFRESH_TOKEN, refreshToken)
                                    putBoolean(PREF_KEY_SIGNUP_REQUIRED, isNewUser)
                                }

                                if (isNewUser) {
                                    startActivity(
                                        Intent(this@SignInActivity, SignUpActivity::class.java)
                                    )
                                    Log.d(TAG, "회원 가입이 필요합니다")

                                    finish()

                                } else {
                                    startActivity(
                                        Intent(this@SignInActivity, HomeActivity::class.java)
                                    )
                                    Log.d(TAG, "로그인 완료")

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
                        Log.e("DEBUG", "로그인 실패", error)

                        Toast.makeText(
                            this,
                            getString(R.string.error_kakao_login), Toast.LENGTH_SHORT
                        ).show()

                    } else if (token != null) {
                        Log.i("DEBUG", "로그인 성공 ${token.idToken}")

                        lifecycleScope.launch {
                            try {
                                val response = RetrofitClient
                                    .retrofitService
                                    .login(LoginRequest(token.idToken!!))
                                    .unwrap()

                                Log.d("DEBUG", response.accessToken)
                                Log.d("DEBUG", response.refreshToken)
                                Log.d("DEBUG", response.isNewUser.toString())

                                accessToken = response.accessToken
                                refreshToken = response.refreshToken
                                isNewUser = response.isNewUser

                                AccessTokenProvider.setAccessToken(accessToken)
                                getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit() {
                                    putString(PREF_KEY_REFRESH_TOKEN, refreshToken)
                                    putBoolean(PREF_KEY_SIGNUP_REQUIRED, isNewUser)
                                }

                                if (isNewUser) {
                                    startActivity(
                                        Intent(this@SignInActivity, SignUpActivity::class.java)
                                    )
                                    Log.d(TAG, "회원 가입이 필요합니다")

                                    finish()

                                } else {
                                    startActivity(
                                        Intent(this@SignInActivity, HomeActivity::class.java)
                                    )
                                    Log.d(TAG, "로그인 완료")

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