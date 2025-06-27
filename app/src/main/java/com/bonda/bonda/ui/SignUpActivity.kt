package com.bonda.bonda.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.edit
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import com.bonda.bonda.R
import com.bonda.bonda.databinding.ActivitySignUpBinding
import com.bonda.bonda.network.ApiClient
import com.bonda.bonda.ui.onboarding.OnboardingActivity
import com.bonda.bonda.util.AccessTokenProvider
import com.bonda.bonda.util.PREFS_NAME
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

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""
        binding.toolbar.setNavigationOnClickListener {
            // TODO 회원 가입을 취소하시겠습니까? 모달표시
            onBackPressedDispatcher.onBackPressed()
        }

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

                    val response = memberService.updateProfile(
                        AccessTokenProvider.getAccessToken()!!
                            .toRequestBody("text/plain".toMediaType()),
                        binding.textEditorUsername.text.toString()
                            .toRequestBody("text/plain".toMediaType()),
                        binaryImage,
                    )
                    Log.d(TAG, response.toString())

                    finish()
                } catch (e: Exception) {
                    Log.d(TAG, "문제가 발생했습니다: ${e.message}")
                }
            }

            prefs.edit() {
                putBoolean(PREF_KEY_SIGNUP_REQUIRED, false)
            }

            val intent = Intent(this, OnboardingActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.profileImage.setOnClickListener {
            ProfileImageSelectorView { uri ->
                profileImage = uri
                binding.profileImage.foreground = null
                binding.profileImage.setImageURI(uri)
            }.show(supportFragmentManager, "SelectImage")
        }

        val primaryButtonTextColor = ContextCompat.getColor(this, R.color.text_default_inverse)
        val primaryButtonBackgroundColor =
            ContextCompat.getColor(this, R.color.surface_accent_primary)
        val disabledButtonTextColor = ContextCompat.getColor(this, R.color.text_default_inverse)
        val disabledButtonBackgroundColor =
            ContextCompat.getColor(this, R.color.surface_default_base)

        binding.textEditorUsername.doOnTextChanged { _, _, _, count ->
            if (count == 0) {
                binding.nextButton.isEnabled = false
                binding.nextButton.setTextColor(disabledButtonTextColor)
                binding.nextButton.setBackgroundColor(disabledButtonBackgroundColor)
            } else {
                if (count > 10) {
                    binding.usernameLengthChecker.setTextColor(
                        resources.getColor(
                            R.color.system_error_primary,
                            null
                        )
                    )
                    binding.nextButton.isEnabled = false
                    binding.nextButton.setTextColor(disabledButtonTextColor)
                    binding.nextButton.setBackgroundColor(disabledButtonBackgroundColor)
                } else {
                    binding.usernameLengthChecker.setTextColor(
                        resources.getColor(
                            R.color.text_default_tertiary,
                            null
                        )
                    )
                    binding.nextButton.isEnabled = true
                    binding.nextButton.setTextColor(primaryButtonTextColor)
                    binding.nextButton.setBackgroundColor(primaryButtonBackgroundColor)
                }
            }
        }
    }
}