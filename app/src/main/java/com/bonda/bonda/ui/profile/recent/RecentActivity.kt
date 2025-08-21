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

        /**
         * 페이지 모서리 inset을 설정합니다
         */
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        /**
         * action bar를 설정합니다
         */
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        /**
         * tab layout에 표시될 페이지를 설정합니다
         */
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

        /**
         * view pager 상단의 tab layout 버튼을 설정합니다
         */
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, pos ->
            tab.text = when (pos) {
                0 -> "도서"
                1 -> "아티클"
                else -> ""
            }
        }.attach()
    }
}