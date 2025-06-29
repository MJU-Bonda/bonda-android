package com.bonda.bonda.ui.profile

import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.lifecycleScope
import com.bonda.bonda.R
import com.bonda.bonda.databinding.ActivityEditProfileBinding
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

    lateinit var binding: ActivityEditProfileBinding
    private var profileImage: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            onBackPressedDispatcher.onBackPressed()
        }

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

    fun uriToBase64(uri: Uri): String {
        val bytes = contentResolver.openInputStream(uri)!!.use { it.readBytes() }
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }
}