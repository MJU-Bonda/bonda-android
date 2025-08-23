package com.bonda.bonda.ui.home.articles

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bonda.bonda.databinding.FragmentHomeArticlesBinding
import com.bonda.bonda.model.ArticleCategory
import com.bonda.bonda.ui.search.SearchActivity
import com.google.android.material.tabs.TabLayoutMediator

class ArticlesFragment : Fragment() {

    private var _binding: FragmentHomeArticlesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeArticlesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /**
         * 검색 버튼을 클릭 시
         */
        binding.searchButton.setOnClickListener {
            val intent = Intent(requireContext(), SearchActivity::class.java)
            startActivity(intent)
        }

        /**
         * viewPager, tabLayout 연결
         */
        binding.vp2.adapter = ArticlesViewPagerAdapter(requireActivity())

        val tabTitles = listOf(
            ArticleCategory.ALL.label,
            ArticleCategory.AUTHOR_OR_PUBLISHER.label,
            ArticleCategory.BOOKSTORE.label,
            ArticleCategory.THEME.label
        )
        TabLayoutMediator(binding.tabLayout, binding.vp2) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}