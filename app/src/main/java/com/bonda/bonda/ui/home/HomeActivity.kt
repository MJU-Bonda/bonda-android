package com.bonda.bonda.ui.home

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.bonda.bonda.R
import com.bonda.bonda.databinding.ActivityHomeBinding
import com.bonda.bonda.ui.home.library.LibraryFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        val navView: BottomNavigationView = binding.navView
        navView.isItemActiveIndicatorEnabled = false
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        navView.setupWithNavController(navController)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        if(intent.getStringExtra("navDest") == "library") {
            openLibraryFragment()
        }
    }

    private fun openLibraryFragment() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.nav_host_fragment_activity_main, LibraryFragment())
            .commitAllowingStateLoss()
    }
}