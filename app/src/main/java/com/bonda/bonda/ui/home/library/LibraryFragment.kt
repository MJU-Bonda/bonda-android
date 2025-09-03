package com.bonda.bonda.ui.home.library

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bonda.bonda.databinding.FragmentHomeLibraryBinding
import com.bonda.bonda.ui.components.BaseFragment
import com.google.android.material.tabs.TabLayoutMediator

class LibraryFragment : BaseFragment() {

    private var _binding: FragmentHomeLibraryBinding? = null
    private val binding get() = _binding!!
    private val vm: LibraryViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentHomeLibraryBinding.inflate(layoutInflater)
        setBaseContent(binding.root)

        binding.viewPager.adapter = object : FragmentStateAdapter(this) {
            private val tabs = listOf("도서", "아티클")
            override fun getItemCount() = tabs.size
            override fun createFragment(position: Int): Fragment {
                return when (position) {
                    0 -> LibraryBooksFragment()
                    1 -> LibraryArticlesFragment()
                    else -> throw IllegalStateException("Invalid position $position")
                }
            }
        }

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, pos ->
            tab.text = when (pos) {
                0 -> "도서"
                1 -> "아티클"
                else -> ""
            }
        }.attach()

        /**
         * 로딩 및 에러 상태 반영
         */
        vm.isLoading.observe(viewLifecycleOwner) { showLoadingView(it) }
        vm.isError.observe(viewLifecycleOwner) { showErrorView(it) }
    }

    /**
     * 재시도 버튼 클릭 시
     */
    override fun onRetry() {
        vm.reloadData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}