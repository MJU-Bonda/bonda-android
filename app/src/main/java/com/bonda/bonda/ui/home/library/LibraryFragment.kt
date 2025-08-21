package com.bonda.bonda.ui.home.library

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bonda.bonda.databinding.FragmentHomeLibraryBinding
import com.google.android.material.tabs.TabLayoutMediator

class LibraryFragment : Fragment() {

    private var _binding: FragmentHomeLibraryBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeLibraryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.viewPager.adapter = object : FragmentStateAdapter(this){
            private val tabs = listOf("도서", "아티클")
            override fun getItemCount() = tabs.size
            override fun createFragment(position: Int): Fragment {
                return LibraryScrollerFragment.newInstance(position)
            }
        }

        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, pos ->
            tab.text = when (pos) {
                0 -> "도서"
                1 -> "아티클"
                else -> ""
            }
        }.attach()

        val initialTab = arguments?.getString("initialTab")
        if (initialTab == "article") {
            binding.viewPager.setCurrentItem(1, false)
            arguments?.remove("initialTab") // 사용 후 제거
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}