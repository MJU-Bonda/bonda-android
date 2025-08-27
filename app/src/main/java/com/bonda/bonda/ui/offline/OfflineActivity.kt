package com.bonda.bonda.ui.offline

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.bonda.bonda.MainActivity
import com.bonda.bonda.databinding.LayoutErrorNetworkBinding
import com.bonda.bonda.model.ERROR_CALLBACK_ACTIVITY
import com.bonda.bonda.model.NetworkStatus

class OfflineActivity : AppCompatActivity() {

    private lateinit var binding: LayoutErrorNetworkBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = LayoutErrorNetworkBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val errorCallbackActivity = intent.getStringExtra(ERROR_CALLBACK_ACTIVITY)

        /**
         * 재시도 버튼 클릭 시 네트워크 연결이 정상적인지 확인하고, 네트워크 연결이 정상적이면 에러 activity 종료
         */
        binding.buttonRetry.setOnClickListener {
            if (!NetworkStatus.isNetworkAvailable(this)) {
                Toast.makeText(this, "인터넷 연결을 확인해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            /**
             * MainActivity에서 호출한 경우 MainActivity를 다시 호출합니다
             */
            if(errorCallbackActivity == "main_activity") {
                Intent(this, MainActivity::class.java).also {
                    startActivity(it)
                    finish()
                }
            }

            finish()
        }
    }

}