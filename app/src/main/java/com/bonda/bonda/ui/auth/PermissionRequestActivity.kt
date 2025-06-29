package com.bonda.bonda.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.bonda.bonda.MainActivity
import com.bonda.bonda.util.PREFS_NAME
import com.bonda.bonda.util.PREF_KEY_PERMISSION_REQUIRED
import com.bonda.bonda.util.TAG
import androidx.core.content.edit
import com.bonda.bonda.databinding.ActivityPermissionRequestBinding

class PermissionRequestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPermissionRequestBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityPermissionRequestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(
                binding.root.paddingLeft,
                systemBars.top,
                binding.root.paddingRight,
                systemBars.bottom
            )
            insets
        }

        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)

        binding.nextButton.setOnClickListener {
            // TODO 권한 설정 로직 추가

            // TODO 권한 설정이 완료되었다면
            if (true) {
                prefs.edit() {
                    putBoolean(PREF_KEY_PERMISSION_REQUIRED, false)
                }
                Log.d(
                    TAG,
                    "application permission required: ${
                        prefs.getBoolean(
                            PREF_KEY_PERMISSION_REQUIRED,
                            true
                        )
                    }"
                )

                startActivity(
                    Intent(this, MainActivity::class.java)
                )

                finish()
            }
        }
    }
}