package com.bonda.bonda.ui.signup

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import com.bonda.bonda.databinding.ActivitySplashBinding
import com.bonda.bonda.network.LoginRequest
import com.bonda.bonda.network.RetrofitClient
import com.bonda.bonda.network.unwrap
import com.kakao.sdk.user.UserApiClient
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivitySplashBinding.inflate(layoutInflater)
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
                    } else if (token != null) {
                        Log.i("DEBUG", "로그인 성공 ${token.idToken}")
                    }
                }
            } else {
                UserApiClient.instance.loginWithKakaoAccount(this) { token, error ->
                    if (error != null) {
                        Log.e("DEBUG", "로그인 실패", error)
                    } else if (token != null) {
                        Log.i("DEBUG", "로그인 성공 ${token.idToken}")

                        lifecycleScope.launch {
                            try {
                                val authResponse = RetrofitClient
                                    .retrofitService
                                    .login(LoginRequest(token.idToken!!))
                                    .unwrap()

                                Log.d("DEBUG", authResponse.accessToken)
                                Log.d("DEBUG", authResponse.refreshToken)
                                Log.d("DEBUG", authResponse.isNewUser.toString())

                                accessToken = authResponse.accessToken
                                refreshToken = authResponse.refreshToken
                                isNewUser = authResponse.isNewUser

                                /**
                                 * ------------------------ >8 ------------------------
                                 */

                                if (isNewUser) {
                                    Intent(this@SplashActivity, SignInActivity::class.java).also {
                                        startActivity(it)
                                    }
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