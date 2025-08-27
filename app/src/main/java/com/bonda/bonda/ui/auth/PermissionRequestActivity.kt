package com.bonda.bonda.ui.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.bonda.bonda.MainActivity
import com.bonda.bonda.model.PREFS_NAME
import com.bonda.bonda.model.PREF_KEY_PERMISSION_REQUIRED
import androidx.core.content.edit
import com.bonda.bonda.databinding.ActivityPermissionRequestBinding

/**
 * 애플리케이션에서 요구하는 권한에 대해 설명하는 액티비티 입니다.
 * 이 페이지에서 권한 요청을 하진 않지만 앱에서 나중에 권한을 요청할 수 있다고 설명합니다.
 * 실질적인 권한 요청은 해당 권한이 필요한 순간에 수행됩니다.
 */
class PermissionRequestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPermissionRequestBinding

    /**
     * private val requestPermissionLauncher: ActivityResultLauncher<String> =
     *         registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
     *             val prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
     *             prefs.edit() { putBoolean(PREF_KEY_PERMISSION_REQUIRED, false) }
     *
     *             val intent = Intent(this, MainActivity::class.java)
     *             startActivity(intent)
     *             finish()
     *         }
     */

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
            prefs.edit() { putBoolean(PREF_KEY_PERMISSION_REQUIRED, false) }
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()

            /**
             * when {
             *                 ContextCompat.checkSelfPermission(
             *                     this,
             *                     Manifest.permission.CAMERA
             *                 ) == PackageManager.PERMISSION_GRANTED -> {
             *                     prefs.edit() { putBoolean(PREF_KEY_PERMISSION_REQUIRED, false) }
             *                     val intent = Intent(this, MainActivity::class.java)
             *                     startActivity(intent)
             *                     finish()
             *                 }
             *                 else -> {
             *                     requestPermissionLauncher.launch(Manifest.permission.CAMERA)
             *                 }
             *             }
             */
        }
    }

}