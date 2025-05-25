package com.bonda.bonda.ui.detail.onboarding

import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.bonda.bonda.R
import com.bonda.bonda.databinding.ActivityOnboardingBinding

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
            OnboardingFragment.newInstance(
                R.string.onboarding1_heading1,
                R.string.onboarding1_heading2,
                R.drawable.img_splash1
            ),
            OnboardingFragment.newInstance(
                R.string.onboarding2_heading1,
                R.string.onboarding2_heading2,
                R.drawable.img_splash2
            ),
            OnboardingFragment.newInstance(
                R.string.onboarding3_heading1,
                R.string.onboarding3_heading2,
                R.drawable.img_splash3
            ),
            OnboardingFragment.newInstance(
                R.string.onboarding4_heading1,
                R.string.onboarding4_heading2,
                R.drawable.img_splash4
            ),
            OnboardingFragment.newInstance(
                R.string.onboarding5_heading1,
                R.string.onboarding5_heading2,
                R.drawable.img_splash5
            )
        )

        binding.viewPager.adapter = object: FragmentStateAdapter(this) {
            override fun getItemCount() = pages.size
            override fun createFragment(pos: Int) = pages[pos]
        }

        // TODO: Tab Indicator 추가
//        TabLayoutMediator(binding.tabIndicator, binding.viewPager) { tab, position ->
//
//        }.attach()

        binding.viewPager.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if (position != pages.lastIndex) {
                    binding.nextButton.text = getText(R.string.onboarding_next_button)
                } else {
                    binding.nextButton.text = getText(R.string.onboarding_next_button_finished)
                }
            }
        })

        binding.nextButton.setOnClickListener {
            if (binding.viewPager.currentItem != pages.lastIndex) {
                binding.viewPager.currentItem++
            } else {
                finish()
            }
        }

        binding.closeButton.setOnClickListener {
            finish()
        }

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (binding.viewPager.currentItem > 0) {
                    binding.viewPager.currentItem--
                }
            }
        })
    }
}