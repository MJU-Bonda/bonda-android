package com.bonda.bonda.ui.search

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.bonda.bonda.databinding.FragmentSearchResultArticleBinding
import com.bonda.bonda.ui.article.ArticleActivity

class SearchResultArticleFragment : Fragment() {

    companion object {
        fun newInstance(): SearchResultArticleFragment = SearchResultArticleFragment()
    }

    private var _binding: FragmentSearchResultArticleBinding? = null
    private val binding get() = _binding!!
    private val vm: SearchViewModel by activityViewModels()
    private lateinit var articleAdapter: ArticleAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchResultArticleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupRecyclerView()

        // 아티클 검색 결과 바인딩
        vm.articlesSearchResult.observe(viewLifecycleOwner) { articles ->
            binding.tvNoResult.visibility = if (articles.isEmpty()) View.VISIBLE else View.GONE
            articleAdapter.submitList(articles)
        }
    }

    private fun setupRecyclerView() {
        articleAdapter = ArticleAdapter { article ->
            val intent = Intent(requireContext(), ArticleActivity::class.java)
            intent.putExtra("article_detail_id", article.id)
            startActivity(intent)
        }
        binding.rv.apply {
            adapter = articleAdapter
            layoutManager = GridLayoutManager(requireContext(), 2)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}