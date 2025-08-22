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
import com.bonda.bonda.AppEvents
import com.bonda.bonda.R
import com.bonda.bonda.databinding.ActivitySignUpBinding
import com.bonda.bonda.network.ApiClient
import com.bonda.bonda.util.AccessTokenProvider
import com.bonda.bonda.util.TAG
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody

class EditProfileActivity : AppCompatActivity() {

    private val memberService = ApiClient.memberService

    lateinit var binding: ActivitySignUpBinding
    private var profileImage: Uri? = null
    private val vm: EditProfileViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.nextButton.text = "저장"

        /**
         * 현재 사용자 정보를 반영합니다
         */
        vm.profileImage.observe(this) {
            if (!it.isNullOrBlank()) {
                binding.profileImage.load(it)
                binding.profileImage.foreground = null
            }
        }
        vm.currentUsername.observe(this) { binding.textEditorUsername.setText(it) }

        /**
         * 액션바 셋업
         */
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

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
                        val rb = RequestBody.create("image/*".toMediaType(), bytes)
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
                    Log.e(TAG, "문제가 발생했습니다: ${e.message}")
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
         * 버튼 활성화 여부를 변경합니다
         */
        vm.newUsername.observe(this) {
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
                    ) // 포커스 없을 때 stroke 제거
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

    }
}
