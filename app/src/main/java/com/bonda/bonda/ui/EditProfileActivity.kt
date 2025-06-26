package com.bonda.bonda.ui

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.core.widget.doOnTextChanged
import com.bonda.bonda.R
import com.bonda.bonda.databinding.ActivityEditProfileBinding

class EditProfileActivity : AppCompatActivity() {

    lateinit var binding: ActivityEditProfileBinding

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
            finish()
        }

        binding.profileImage.setOnClickListener {
            ProfileImageSelectorView().show(supportFragmentManager, "SelectImage")
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