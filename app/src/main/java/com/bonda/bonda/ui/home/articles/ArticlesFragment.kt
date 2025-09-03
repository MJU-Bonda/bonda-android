package com.bonda.bonda.ui.home.articles

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.bonda.bonda.R
import com.bonda.bonda.databinding.FragmentHomeArticlesBinding
import com.bonda.bonda.model.ArticleCategory
import com.bonda.bonda.ui.components.BaseFragment
import com.bonda.bonda.ui.search.SearchActivity
import com.google.android.material.tabs.TabLayoutMediator

class ArticlesFragment : BaseFragment() {

    private var _binding: FragmentHomeArticlesBinding? = null
    private val binding get() = _binding!!
    private val vm: ArticlesViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // 1. BaseFragment의 onCreateView를 호출하여 기본 레이아웃(layout_base.xml)을 먼저 생성합니다.
        val baseView = super.onCreateView(inflater, container, savedInstanceState)

        // 2. BaseFragment의 레이아웃 안에서 콘텐츠를 담을 content_frame을 찾습니다.
        val contentFrame = baseView?.findViewById<ViewGroup>(R.id.content_frame)

        // 3. ArticlesFragment의 콘텐츠 레이아웃을 content_frame에 직접 인플레이트하고 바인딩합니다.
        //    세 번째 인자(attachToRoot)를 true로 설정하여 즉시 붙여줍니다.
        _binding = FragmentHomeArticlesBinding.inflate(inflater, contentFrame, true)

        // 4. 모든 것이 결합된 최종 뷰(baseView)를 반환합니다.
        return baseView
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

        /**
         * 에러 및 로딩 상태 반영
         */
        vm.isLoading.observe(viewLifecycleOwner) { showLoadingView(it) }
        vm.isError.observe(viewLifecycleOwner) { showErrorView(it) }
    }

    override fun onRetry() {
        vm.getArticles()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}