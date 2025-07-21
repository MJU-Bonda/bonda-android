package com.bonda.bonda.ui.profile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import com.bonda.bonda.MainActivity
import com.bonda.bonda.databinding.ActivitySettingsBinding
import com.bonda.bonda.network.ApiClient
import com.bonda.bonda.network.model.auth.LogoutRequest
import com.bonda.bonda.util.AccessTokenProvider
import com.bonda.bonda.util.PREFS_NAME
import com.bonda.bonda.util.PREF_KEY_REFRESH_TOKEN
import com.bonda.bonda.util.PREF_KEY_SIGNUP_REQUIRED
import com.bonda.bonda.util.TAG
import kotlinx.coroutines.launch

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private val authService = ApiClient.authService

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

        binding.buttonEditProfile.setOnClickListener {
            startActivity(Intent(this, EditProfileActivity::class.java))
        }

        binding.buttonLogout.setOnClickListener {
            DialogView.newInstance(
                requestKey = "logout",
                message = "로그아웃하시겠습니까?",
                confirmText = "로그아웃",
                cancelText = "취소"
            ).show(supportFragmentManager, "logout_dialog")
        }

        binding.buttonWithdrawal.setOnClickListener {
            DialogView.newInstance(
                requestKey = "withdrawal",
                message = "정말 탈퇴하시겠습니까?",
                confirmText = "탈퇴",
                cancelText = "취소"
            ).show(supportFragmentManager, "withdrawal_dialog")
        }

        supportFragmentManager.setFragmentResultListener(
            "logout", this
        ) { _, bundle ->
            if (bundle.getBoolean("isConfirmed", false)) {
                performLogout()
            }
        }
        supportFragmentManager.setFragmentResultListener(
            "withdrawal", this
        ) { _, bundle ->
            if (bundle.getBoolean("isConfirmed", false)) {
                performWithdrawal()
            }
        }
    }

    private fun performLogout() {
        lifecycleScope.launch {
            try {
                val res = authService.logout(
                    LogoutRequest(
                        AccessTokenProvider.getAccessToken()!!,
                        getSharedPreferences(PREFS_NAME, MODE_PRIVATE).getString(
                            PREF_KEY_REFRESH_TOKEN, null
                        )!!
                    )
                ).unwrapOrThrow()
                Log.d(TAG, res.toString())
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
            } finally {
                Toast.makeText(this@SettingsActivity, "로그아웃되었습니다", Toast.LENGTH_LONG).show()

                AccessTokenProvider.removeAccessToken()
                getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit() {
                    remove(PREF_KEY_REFRESH_TOKEN)
                    remove(PREF_KEY_SIGNUP_REQUIRED)
                }

                val intent = Intent(this@SettingsActivity, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        }
    }

    private fun performWithdrawal() {
        lifecycleScope.launch {
            try {
                val res = authService.withdrawal().unwrapOrThrow()
                Log.d(TAG, res.toString())

                Toast.makeText(this@SettingsActivity, "탈퇴되었습니다", Toast.LENGTH_LONG).show()

                AccessTokenProvider.removeAccessToken()
                getSharedPreferences(PREFS_NAME, MODE_PRIVATE).edit() {
                    remove(PREF_KEY_REFRESH_TOKEN)
                    remove(PREF_KEY_SIGNUP_REQUIRED)
                }

                val intent = Intent(this@SettingsActivity, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            } catch (e: Exception) {
                Log.e(TAG, e.toString())
            }
        }
    }
}