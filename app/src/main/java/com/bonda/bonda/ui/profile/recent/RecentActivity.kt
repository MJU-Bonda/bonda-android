package com.bonda.bonda.ui.profile.recent

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bonda.bonda.databinding.ActivityRecentActivityBinding
import com.bonda.bonda.ui.profile.recent.articles.ArticlesFragment
import com.bonda.bonda.ui.profile.recent.books.BooksFragment
import com.google.android.material.tabs.TabLayoutMediator

class RecentActivity :AppCompatActivity() {

    lateinit var binding: ActivityRecentActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityRecentActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        binding.viewPager.adapter = object : FragmentStateAdapter(this) {
            private val tabs = listOf("도서", "아티클")
            override fun getItemCount() = tabs.size
            override fun createFragment(position: Int): Fragment =
                when (position) {
                    0 -> BooksFragment()
                    1 -> ArticlesFragment()
                    else -> throw IndexOutOfBoundsException()
                }
        }

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, pos ->
            tab.text = when (pos) {
                0 -> "도서"
                1 -> "아티클"
                else -> ""
            }
        }.attach()
    }
}