package com.bonda.bonda.ui.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bonda.bonda.databinding.FragmentSearchResultAllBinding

class SearchResultFragment: Fragment() {
    companion object {
        private const val ARG_CATEGORY = "arg_category"

        fun newInstance(category: String): SearchResultFragment =
            SearchResultFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_CATEGORY, category)
                }
            }
    }

    private val category: String by lazy {
        requireArguments().getString(ARG_CATEGORY)!!
    }

    private var _binding: FragmentSearchResultAllBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchResultAllBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val vm = ViewModelProvider(this)[SearchViewModel::class.java]

        vm.bookSearchResult.observe(viewLifecycleOwner) {
            if(it.isEmpty()) binding.tvBooksNoResult.visibility = View.VISIBLE
            else binding.tvBooksNoResult.visibility = View.GONE
        }
        vm.articleSearchResult.observe(viewLifecycleOwner) {
            if(it.isEmpty()) binding.tvArticlesNoResult.visibility = View.VISIBLE
            else binding.tvArticlesNoResult.visibility = View.GONE
        }


        // 받은 category 값으로 화면 구성
        when (category) {
            "전체"   -> {

            }
            "도서"   -> {
                binding.articlesContainer.visibility = View.GONE
            }
            "아티클" -> {
                binding.booksContainer.visibility = View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}