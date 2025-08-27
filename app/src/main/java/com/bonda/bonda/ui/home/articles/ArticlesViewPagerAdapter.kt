package com.bonda.bonda.ui.home.articles

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bonda.bonda.model.ArticleCategory

class ArticlesViewPagerAdapter(fragmentActivity: FragmentActivity) :
    FragmentStateAdapter(fragmentActivity) {

    private val fragmentsCategory = listOf(
        ArticleCategory.ALL,
        ArticleCategory.AUTHOR_OR_PUBLISHER,
        ArticleCategory.BOOKSTORE,
        ArticleCategory.THEME
    )

    override fun getItemCount(): Int {
        return fragmentsCategory.size
    }

    override fun createFragment(position: Int): Fragment {
        return ArticlesListFragment.newInstance(fragmentsCategory[position])
    }

}