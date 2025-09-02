package com.bonda.bonda.ui.auth

import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
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
import com.bonda.bonda.model.AccessTokenProvider
import com.bonda.bonda.model.PREFS_NAME
import com.bonda.bonda.model.PREF_KEY_REFRESH_TOKEN
import com.bonda.bonda.model.PREF_KEY_SIGNUP_REQUIRED
import com.bonda.bonda.model.TAG
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class SignUpActivity : AppCompatActivity() {

    private val memberService = ApiClient.memberService

    private lateinit var binding: ActivitySignUpBinding
    private var profileImage: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)

        /**
         * 액티비티 인셋 설정
         */
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
                        val rb = bytes.toRequestBody("image/*".toMediaTypeOrNull())
                        MultipartBody.Part.createFormData(
                            name = "profileImage",
                            filename = "profile.jpg",
                            body = rb
                        )
                    }

                    memberService.updateProfile(
                        AccessTokenProvider.getAccessToken()!!
                            .toRequestBody("text/plain".toMediaType()),
                        binding.textEditorUsername.text.toString()
                            .toRequestBody("text/plain".toMediaType()),
                        binaryImage,
                    )

                    /**
                     * 로그인에 성공하면 signup required 를 false로 설정합니다
                     */
                    prefs.edit() { putBoolean(PREF_KEY_SIGNUP_REQUIRED, false) }
                    val intent = Intent(this@SignUpActivity, OnboardingActivity::class.java)
                    startActivity(intent)
                    finish()
                } catch (e: Exception) {
                    Log.e(TAG, "SignUpActivity.kt::nextButton.setOnClickListener", e)
                    Toast.makeText(this@SignUpActivity, "인터넷 연결을 확인해주세요", Toast.LENGTH_SHORT).show()
                }
            }
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
         * 닉네임 입력기 값 변경을 감지하여 버튼 활성화 및 텍스트 색상을 변경합니다.
         * 입력된 닉네임 텍스트 길이가 0인 경우 다음 버튼이 비활성화 됩니다.
         * 입력된 닉네임 텍스트 길이가 10을 초과하면 다음 버튼이 비활성화되고, 글자 수 제한 텍스트 색상이 빨간색으로 표시됩니다.
         */
        binding.textEditorUsername.doOnTextChanged { text, _, _, _ ->
            val inputText = text.toString()
            val isLengthExceeded = inputText.length > 10

            if (isLengthExceeded || inputText.isBlank()) {
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

            if (isLengthExceeded) {
                binding.usernameLengthChecker.setTextColor(
                    ContextCompat.getColor(this, R.color.system_error_primary)
                )
            } else {
                binding.usernameLengthChecker.setTextColor(
                    ContextCompat.getColor(this, R.color.text_default_tertiary)
                )
            }
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
                    )
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

        /**
         * 초기 버튼 상태를 비활성화로 설정
         */
        binding.nextButton.isEnabled = false
        binding.nextButton.setBackgroundColor(
            ContextCompat.getColor(
                this,
                R.color.surface_default_base
            )
        )
    }

}