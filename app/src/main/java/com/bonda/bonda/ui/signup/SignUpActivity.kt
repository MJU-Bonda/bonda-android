package com.bonda.bonda.ui.signup

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.core.widget.doOnTextChanged
import com.bonda.bonda.R
import com.bonda.bonda.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val primaryButtonTextColor = ContextCompat.getColor(this, R.color.text_default_inverse)
        val primaryButtonBackgroundColor = ContextCompat.getColor(this, R.color.surface_accent_primary)
        val disabledButtonTextColor = ContextCompat.getColor(this, R.color.text_default_inverse)
        val disabledButtonBackgroundColor = ContextCompat.getColor(this, R.color.surface_default_base)

        binding.textEditorUsername.doOnTextChanged { _, _, _, count ->
            if(count == 0){
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

        binding.nextButton.setOnClickListener {
            finish()
        }
    }
}