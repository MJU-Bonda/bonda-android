package com.bonda.bonda.ui.home

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.bonda.bonda.R
import com.bonda.bonda.databinding.ActivityHomeBinding
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding
    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /**
         * 액티비티 화면 인셋 적용
         * 하단은 navigation bar가 자체적으로 inset을 적용하니 0으로 설정
         */
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        val navView: BottomNavigationView = binding.navView
        navView.isItemActiveIndicatorEnabled = false
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        navView.setupWithNavController(navController)

        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent) {
        val destinationId = intent.getIntExtra("destination_id", 0)
        if (destinationId == 0) return

        /**
         * ViewModel에 탭 위치 요청
         */
        if (intent.hasExtra("library_tab_position")) {
            val tabPosition = intent.getIntExtra("library_tab_position", 0)
            homeViewModel.requestLibraryTab(tabPosition)
        }

        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val navOptions = NavOptions.Builder()
            .setLaunchSingleTop(true)
            .setRestoreState(true)
            .setPopUpTo(navController.graph.startDestinationId, saveState = true, inclusive = false)
            .build()

        /**
         * 인자(args) 없이 화면만 이동
         */
        navController.navigate(destinationId, null, navOptions)

        /**
         * 소모한 extra 제거
         */
        intent.removeExtra("destination_id")
        intent.removeExtra("library_tab_position")
    }

}