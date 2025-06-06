package com.bonda.bonda.ui.main.profile.edit

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.core.widget.doOnTextChanged
import com.bonda.bonda.R
import com.bonda.bonda.databinding.ActivitySignupBinding

class SignupActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)



        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        binding.textEditorUsername.doOnTextChanged { text, start, before, count ->
            if (count > 10) {
                binding.usernameLengthChecker.setTextColor(resources.getColor(R.color.system_error_primary, null))
            } else {
                binding.usernameLengthChecker.setTextColor(resources.getColor(R.color.text_default_tertiary, null))
            }
        }
    }
}