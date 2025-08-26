package com.bonda.bonda.ui.profile.recent.articles

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.bonda.bonda.databinding.FragmentRecentActivityBinding
import com.bonda.bonda.ui.article.ArticleActivity

class ArticlesFragment : Fragment() {

    private var _binding: FragmentRecentActivityBinding? = null
    private val binding get() = _binding!!
    private val vm: ArticlesViewModel by viewModels()

    private val adapter by lazy {
        ArticleAdapter { article ->
            val intent = Intent(requireContext(), ArticleActivity::class.java).apply {
                putExtra("article_detail_id", article.id)
            }
            startActivity(intent)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecentActivityBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.container.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.container.adapter = adapter

        vm.getArticles()

        vm.isLoading.observe(viewLifecycleOwner) { binding.progressIndicator.isVisible = it }
        vm.isEmpty.observe(viewLifecycleOwner) { binding.emptyArticleListText.isVisible = it }
        vm.isError.observe(viewLifecycleOwner) { binding.errorNetwork.root.isVisible= it }
        vm.articles.observe(viewLifecycleOwner) {
            adapter.submitList(it)
            binding.container.isVisible = it.isNotEmpty()
        }

        binding.errorNetwork.buttonRetry.setOnClickListener { vm.getArticles() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}