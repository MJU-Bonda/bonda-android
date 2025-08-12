package com.bonda.bonda.ui.auth

import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import com.bonda.bonda.MainActivity
import com.bonda.bonda.R
import com.bonda.bonda.databinding.ActivitySignUpBinding
import com.bonda.bonda.network.ApiClient
import com.bonda.bonda.ui.auth.onboarding.OnboardingActivity
import com.bonda.bonda.ui.profile.ProfileImageSelectorView
import com.bonda.bonda.util.AccessTokenProvider
import com.bonda.bonda.util.PREFS_NAME
import com.bonda.bonda.util.PREF_KEY_REFRESH_TOKEN
import com.bonda.bonda.util.PREF_KEY_SIGNUP_REQUIRED
import com.bonda.bonda.util.TAG
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class SignUpActivity : AppCompatActivity() {

    private val memberService = ApiClient.memberService

    private lateinit var binding: ActivitySignUpBinding
    private var profileImage: Uri? = null
    private val vm: SignUpViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        /**
         * 액션바 구성 설정
         */
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""
        binding.toolbar.setNavigationOnClickListener {
            Toast.makeText(this, "로그아웃되었습니다", Toast.LENGTH_LONG).show()

            AccessTokenProvider.removeAccessToken()
            getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit() {
                remove(PREF_KEY_REFRESH_TOKEN)
                remove(PREF_KEY_SIGNUP_REQUIRED)
            }

            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        /**
         * 회원 가입 버튼
         */
        binding.nextButton.setOnClickListener {
            lifecycleScope.launch {
                try {
                    val binaryImage = profileImage?.let { uri ->
                        val bytes =
                            this@SignUpActivity.contentResolver.openInputStream(uri)!!
                                .use { it.readBytes() }
                        val rb = RequestBody.create("image/*".toMediaType(), bytes)
                        MultipartBody.Part.createFormData(
                            name = "profileImage",
                            filename = "profile.jpg",
                            body = rb
                        )
                    }

                    val res = memberService.updateProfile(
                        AccessTokenProvider.getAccessToken()!!
                            .toRequestBody("text/plain".toMediaType()),
                        binding.textEditorUsername.text.toString()
                            .toRequestBody("text/plain".toMediaType()),
                        binaryImage,
                    )
                    Log.d(TAG, res.toString())

                    finish()
                } catch (e: Exception) {
                    Log.d(TAG, "문제가 발생했습니다: ${e.message}")
                }
            }

            /**
             * 로그인 성공하면 signup required 를 false로 설정합니다
             */
            prefs.edit() {
                putBoolean(PREF_KEY_SIGNUP_REQUIRED, false)
            }

            val intent = Intent(this, OnboardingActivity::class.java)
            startActivity(intent)
            finish()
        }

        /**
         * 프로필 이미지 선택
         */
        binding.profileImage.setOnClickListener {
            ProfileImageSelectorView { uri ->
                profileImage = uri
                binding.profileImage.foreground = null
                binding.profileImage.setImageURI(uri)
            }.show(supportFragmentManager, "SelectImage")
        }

        /**
         * 버튼 활성화 여부를 변경합니다
         */
        vm.username.observe(this) {
            if (it.isNullOrBlank() || it.length > 10) {
                binding.nextButton.isEnabled = false
                binding.nextButton.setBackgroundColor(
                    ContextCompat.getColor(
                        this,
                        R.color.surface_default_base
                    )
                )
            } else {
                binding.nextButton.isEnabled = true
                binding.nextButton.setBackgroundColor(
                    ContextCompat.getColor(
                        this,
                        R.color.surface_accent_primary
                    )
                )
            }

            if (it.length > 10) {
                binding.usernameLengthChecker.setTextColor(
                    resources.getColor(
                        R.color.system_error_primary,
                        null
                    )
                )
            } else {
                binding.usernameLengthChecker.setTextColor(
                    resources.getColor(
                        R.color.text_default_tertiary,
                        null
                    )
                )
            }
        }

        /**
         * 닉네임 입력기 값 변경을 감지합니다
         */
        binding.textEditorUsername.doOnTextChanged { text, _, _, _ ->
            vm.setUsername(text.toString())
        }

        /**
         * 닉네임 입력기 백그라운드를 설정하고, focus시 stroke를 추가합니다
         */
        binding.textEditorUsername.setOnFocusChangeListener { _, hasFocus ->
            val drawable = GradientDrawable().apply {
                shape = GradientDrawable.RECTANGLE
                cornerRadius = 8f * resources.displayMetrics.density
                if (hasFocus) {
                    setStroke(
                        1 * resources.displayMetrics.density.toInt(),
                        ContextCompat.getColor(this@SignUpActivity, R.color.border_default_tertiary)
                    )
                } else {
                    setStroke(
                        0,
                        ContextCompat.getColor(this@SignUpActivity, R.color.border_default_tertiary)
                    ) // 포커스 없을 때 stroke 제거
                }
                setColor(
                    ContextCompat.getColor(
                        this@SignUpActivity,
                        R.color.surface_default_primary
                    )
                )
            }
            binding.textEditorUsername.background = drawable
        }
        binding.textEditorUsername.background = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 8f * resources.displayMetrics.density
            setColor(ContextCompat.getColor(this@SignUpActivity, R.color.surface_default_primary))
        }

    }
}