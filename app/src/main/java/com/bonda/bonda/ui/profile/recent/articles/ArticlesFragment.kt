package com.bonda.bonda.ui.profile.recent.articles

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.bonda.bonda.databinding.FragmentRecentBooksBinding
import com.bonda.bonda.ui.article.ArticleActivity

class ArticlesFragment : Fragment() {

    private var _binding: FragmentRecentBooksBinding? = null
    private val binding get() = _binding!!

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
        _binding = FragmentRecentBooksBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.root.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.root.adapter = adapter

        val vm = ViewModelProvider(this) [ArticlesViewModel::class.java]
        vm.articles.observe(viewLifecycleOwner) { adapter.submitList(it) }
        vm.getArticles()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}