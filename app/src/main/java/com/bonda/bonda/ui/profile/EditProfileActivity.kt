package com.bonda.bonda.ui.profile

import android.graphics.drawable.GradientDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import coil3.load
import com.bonda.bonda.model.AppEvents
import com.bonda.bonda.R
import com.bonda.bonda.databinding.ActivitySignUpBinding
import com.bonda.bonda.network.ApiClient
import com.bonda.bonda.model.AccessTokenProvider
import com.bonda.bonda.model.TAG
import com.bonda.bonda.ui.home.profile.ProfileViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class EditProfileActivity : AppCompatActivity() {

    private val memberService = ApiClient.memberService

    lateinit var binding: ActivitySignUpBinding
    private var profileImage: Uri? = null
    private val vm: ProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.nextButton.text = "저장"

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
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

        /**
         * 현재 사용자 정보를 반영합니다
         */
        vm.profileImage.observe(this) {
            if (!it.isNullOrBlank()) {
                binding.profileImage.load(it)
                binding.profileImage.foreground = null
            }
        }
        vm.username.observe(this) { binding.textEditorUsername.setText(it) }

        /**
         * 프로필 변경 버튼 클릭
         */
        binding.nextButton.setOnClickListener {
            lifecycleScope.launch {
                try {
                    val binaryImage = profileImage?.let { uri ->
                        val bytes =
                            this@EditProfileActivity.contentResolver.openInputStream(uri)!!
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
                     * profile 데이터를 다시 불러오도록 신호 전달
                     */
                    AppEvents.profileUpdated.emit(Unit)
                    finish()
                } catch (e: Exception) {
                    Log.e(TAG, "EditProfileActivity.kt::nextButton.setOnClickListener", e)
                }
            }
        }

        /**
         * 프로필 이미지 아이콘 클릭
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
                        ContextCompat.getColor(
                            this@EditProfileActivity,
                            R.color.border_default_tertiary
                        )
                    )
                } else {
                    setStroke(
                        0,
                        ContextCompat.getColor(
                            this@EditProfileActivity,
                            R.color.border_default_tertiary
                        )
                    )
                }
                setColor(
                    ContextCompat.getColor(
                        this@EditProfileActivity,
                        R.color.surface_default_primary
                    )
                )
            }
            binding.textEditorUsername.background = drawable
        }
        binding.textEditorUsername.background = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            cornerRadius = 8f * resources.displayMetrics.density
            setColor(
                ContextCompat.getColor(
                    this@EditProfileActivity,
                    R.color.surface_default_primary
                )
            )
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