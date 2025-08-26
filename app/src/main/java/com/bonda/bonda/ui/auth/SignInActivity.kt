package com.bonda.bonda.ui.auth

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import com.bonda.bonda.ui.home.HomeActivity
import com.bonda.bonda.util.*
import com.kakao.sdk.user.UserApiClient
import kotlinx.coroutines.launch
import androidx.core.content.edit
import androidx.core.net.toUri
import com.bonda.bonda.R
import com.bonda.bonda.databinding.ActivitySignInBinding
import com.bonda.bonda.model.AccessTokenProvider
import com.bonda.bonda.network.ApiClient
import com.bonda.bonda.network.model.auth.LoginRequest

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

        /**
         * 카카오로 로그인 하기 버튼 로직
         */
        binding.signInButton.setOnClickListener {
            UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
                Log.d(TAG, "$tokenInfo, $error")
            }

            /**
             * 카카오톡이 설치 되어 있는 경우
             */
            if (UserApiClient.instance.isKakaoTalkLoginAvailable(this)) {
                UserApiClient.instance.loginWithKakaoTalk(this) { token, error ->
                    /**
                     * 카카오톡이 설치되어있고 로그인이 안되어 있는 경우 브라우저 로그인을 호출합니다
                     */
                    if (error != null) {
                        UserApiClient.instance.loginWithKakaoAccount(this) { token, error ->
                            if (token != null) {
                                lifecycleScope.launch {
                                    try {
                                        grantLogin(token.idToken!!, this@SignInActivity)
                                    } catch (e: Exception) {
                                        Log.e("DEBUG", e.toString())
                                    }
                                }
                            }
                        }
                    }
                    /**
                     * 카카오톡이 설치되어있고 로그인이 되어 있는 경우 카카오톡 토큰을 가져옵니다
                     */
                    else if (token != null) {
                        lifecycleScope.launch {
                            try {
                                grantLogin(token.idToken!!, this@SignInActivity)
                            } catch (e: Exception) {
                                Log.e("DEBUG", e.toString())
                            }
                        }
                    }
                }
            }
            /**
             * 카카오톡이 설치되어있지 않은 경우 브라우저 로그인을 호출합니다
             */
            else {
                Log.d(TAG, "로그인을 시도합니다")
                UserApiClient.instance.loginWithKakaoAccount(this) { token, _ ->
                    if (token != null) {
                        lifecycleScope.launch {
                            try {
                                grantLogin(token.idToken!!, this@SignInActivity)
                            } catch (e: Exception) {
                                Log.e("DEBUG", e.toString())
                            }
                        }
                    }
                }
            }
        }

        /**
         * 이용약관 및 개인정보처리방침 버튼
         */
        val fullText = "시작할 경우, 본다의 서비스 이용약관 및 개인정보 보호정책에 동의하게 됩니다."
        val spannable = SpannableString(fullText)

        val termsText = "서비스 이용약관"
        val termsStart = fullText.indexOf(termsText)
        spannable.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                val builder = CustomTabsIntent.Builder()
                val customTabsIntent = builder.build()
                customTabsIntent.launchUrl(this@SignInActivity, BONDA_TERMS_OF_POLICY_URL.toUri())
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color =
                    ContextCompat.getColor(this@SignInActivity, R.color.text_default_tertiary)
                ds.isUnderlineText = true // 밑줄
            }
        }, termsStart, termsStart + termsText.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        val privacyText = "개인정보 보호정책"
        val privacyStart = fullText.indexOf(privacyText)
        spannable.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                val builder = CustomTabsIntent.Builder()
                val customTabsIntent = builder.build()
                customTabsIntent.launchUrl(this@SignInActivity, BONDA_PRIVACY_POLICY_URL.toUri())
            }

            override fun updateDrawState(ds: TextPaint) {
                super.updateDrawState(ds)
                ds.color =
                    ContextCompat.getColor(this@SignInActivity, R.color.text_default_tertiary)
                ds.isUnderlineText = true // 밑줄
            }
        }, privacyStart, privacyStart + privacyText.length, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.tv.text = spannable
        binding.tv.movementMethod = LinkMovementMethod.getInstance()
        binding.tv.highlightColor = Color.TRANSPARENT

    }

    /**
     * 로그인용 함수 정의
     */
    suspend fun grantLogin(token: String, context: Context) {
        val response = authService.login(LoginRequest(token))
            .unwrapOrThrow()

        val accessToken = response.accessToken
        val refreshToken = response.refreshToken
        val isNewUser = response.isNewUser

        AccessTokenProvider.setAccessToken(accessToken)
        getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit() {
            putString(PREF_KEY_REFRESH_TOKEN, refreshToken)
            putBoolean(PREF_KEY_SIGNUP_REQUIRED, isNewUser)
        }

        if (isNewUser) {
            val intent = Intent(context, SignUpActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            val intent = Intent(context, HomeActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

}