package com.bonda.bonda.ui.detail.onboarding

import android.os.Bundle
import android.os.Debug
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.bonda.bonda.R
import com.bonda.bonda.databinding.ActivityOnboardingBinding
import com.google.android.material.tabs.TabLayoutMediator

class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val pages = listOf(
            OnboardingFragment.newInstance(R.string.onboarding1_heading1, R.string.onboarding1_heading2),
            OnboardingFragment.newInstance(R.string.onboarding2_heading1, R.string.onboarding2_heading2)
        )

        binding.viewPager.adapter = object: FragmentStateAdapter(this) {
            override fun getItemCount() = pages.size
            override fun createFragment(pos: Int) = pages[pos]
        }

        TabLayoutMediator(binding.tabIndicator, binding.viewPager) { _, _ -> }.attach()

        binding.viewPager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if (position == pages.lastIndex) {

                }
            }
        })
    }
}