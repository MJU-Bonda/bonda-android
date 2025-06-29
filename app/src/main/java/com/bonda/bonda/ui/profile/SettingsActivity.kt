package com.bonda.bonda.ui.profile

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.bonda.bonda.MainActivity
import com.bonda.bonda.databinding.ActivitySettingsBinding
import com.bonda.bonda.util.AccessTokenProvider
import com.bonda.bonda.util.PREFS_NAME
import com.bonda.bonda.util.PREF_KEY_REFRESH_TOKEN
import com.bonda.bonda.util.PREF_KEY_SIGNUP_REQUIRED

class SettingsActivity: AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivitySettingsBinding.inflate(layoutInflater)
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

        binding.buttonEditProfile.setOnClickListener{
            startActivity(Intent(this, EditProfileActivity::class.java))
        }

        binding.buttonLogout.setOnClickListener {
            AccessTokenProvider.removeAccessToken()

            getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit(){
                remove(PREF_KEY_REFRESH_TOKEN)
                remove(PREF_KEY_SIGNUP_REQUIRED)
            }

            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

            /**
             * TODO : 스낵바 제작되면 로그아웃되었습니다 표시하기
             */
            Toast.makeText(this, "로그아웃되었습니다", Toast.LENGTH_LONG).show()
            startActivity(intent)
        }

        binding.buttonWithdrawal.setOnClickListener {
            WithdrawalDialogView().show(supportFragmentManager, "logout")
        }
    }
}