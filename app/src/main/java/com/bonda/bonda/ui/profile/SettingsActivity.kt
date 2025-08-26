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
import com.bonda.bonda.model.AccessTokenProvider
import com.bonda.bonda.model.PREFS_NAME
import com.bonda.bonda.model.PREF_KEY_REFRESH_TOKEN
import com.bonda.bonda.model.PREF_KEY_SIGNUP_REQUIRED
import com.bonda.bonda.model.TAG
import com.kakao.sdk.user.UserApiClient
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

        /**
         * 로그아웃 dialog 표시
         */
        binding.buttonLogout.setOnClickListener {
            DialogView.newInstance(
                requestKey = "logout",
                message = "로그아웃하시겠습니까?",
                confirmText = "네",
                cancelText = "아니요"
            ).show(supportFragmentManager, "logout_dialog")
        }

        /**
         * 회원탈퇴 dialog 표시
         */
        binding.buttonWithdrawal.setOnClickListener {
            DialogView.newInstance(
                requestKey = "withdrawal",
                message = "Bonda를 떠나기 전, 확인해 주세요.",
                message2 = "탈퇴하시면 계정과 저장된 콘텐츠, 작성한 기록이 모두 삭제되며, 다시 복구할 수 없습니다.",
                confirmText = "탈퇴하기",
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

    /**
     * 로그아웃 api 요청 로직
     */
    private fun performLogout() {
        lifecycleScope.launch {
            try {
                UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
                    Log.d(TAG, "$tokenInfo, $error")
                }
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

    /**
     * 회원탈퇴 api 요청 로직
     */
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