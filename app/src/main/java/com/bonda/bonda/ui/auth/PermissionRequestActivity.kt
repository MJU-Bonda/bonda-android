package com.bonda.bonda.ui.auth

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.bonda.bonda.MainActivity
import com.bonda.bonda.util.PREFS_NAME
import com.bonda.bonda.util.PREF_KEY_PERMISSION_REQUIRED
import androidx.core.content.edit
import com.bonda.bonda.databinding.ActivityPermissionRequestBinding

class PermissionRequestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPermissionRequestBinding

    private val requestPermissionLauncher: ActivityResultLauncher<String> =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
            prefs.edit() { putBoolean(PREF_KEY_PERMISSION_REQUIRED, false) }

            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPermissionRequestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)

        /**
         * 액티비티 인셋
         */
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        /**
         * '확인했어요' 버튼 클릭 시 권한 요청 한번 수행 후 activity 종료
         */
        binding.nextButton.setOnClickListener {
            when {
                ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED -> {
                    prefs.edit() { putBoolean(PREF_KEY_PERMISSION_REQUIRED, false) }
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                }
                else -> {
                    requestPermissionLauncher.launch(Manifest.permission.CAMERA)
                }
            }
        }
    }

}