package com.bonda.bonda.ui.home.articles

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import coil3.load
import com.bonda.bonda.R
import com.bonda.bonda.databinding.FragmentHomeArticlesBinding
import com.bonda.bonda.databinding.ViewArticleBinding
import com.bonda.bonda.databinding.ViewChipWriterBinding
import com.bonda.bonda.databinding.ViewChipThemeBinding
import com.bonda.bonda.ui.article.ArticleActivity
import com.bonda.bonda.ui.search.SearchActivity

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

        binding.searchButton.setOnClickListener {
            startActivity(Intent(requireContext(), SearchActivity::class.java))
        }

        val viewModel = ViewModelProvider(this)[ArticlesViewModel::class.java]

        viewModel.articles.observe(viewLifecycleOwner) { list ->
            binding.articlesContainer.removeAllViews()

            var lastAddedViewId: Int? = null

            list.forEach { article ->
                val itemBinding = ViewArticleBinding.inflate(
                    layoutInflater,
                    binding.articlesContainer,
                    false
                )

                itemBinding.root.id = View.generateViewId()

                // view-model binding
                itemBinding.articleImage.load(article.coverImage) {
                    // TODO 커버 이미지 오류 처리 필요

                    listener(
                        onError = { _, throwable ->
                            Log.e("Coil", "load failed$throwable")
                        }
                    )
                }
                itemBinding.articleTitle.text = article.title
                itemBinding.articleSubtitle.text = article.subTitle

                if (article.category == "테마") {
                    ViewChipThemeBinding.inflate(
                        layoutInflater,
                        itemBinding.articleChipGroup,
                        true
                    )
                } else if (article.category == "작가/출판사") {
                    ViewChipWriterBinding.inflate(
                        layoutInflater,
                        itemBinding.articleChipGroup,
                        true
                    )
                }

                if (article.isSaved) {
                    itemBinding.articleButtonBookmark.apply {
                        setImageResource(R.drawable.ic_action_bookmark_fill_24dp)
                        setOnClickListener { viewModel.toggleSaved(article.id) }
                    }
                } else {
                    itemBinding.articleButtonBookmark.apply {
                        setImageResource((R.drawable.ic_action_bookmark_empty_24dp))
                        setOnClickListener { viewModel.toggleSaved(article.id) }
                    }
                }

                // setup layout constraint
                val params = itemBinding.root.layoutParams as ConstraintLayout.LayoutParams

                params.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
                params.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID

                if (lastAddedViewId != null) {
                    params.topToBottom = lastAddedViewId!!
                } else {
                    params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                }

                val marginDp = 24
                val density = itemBinding.root.context.resources.displayMetrics.density
                params.topMargin = (marginDp * density).toInt()

                itemBinding.root.layoutParams = params

                // start new article detail activity
                itemBinding.root.setOnClickListener {
                    val intent = Intent(requireContext(), ArticleActivity::class.java)
                    intent.putExtra("article_detail_id", article.id)
                    Log.d("DEBUG", "start_activity_article_detail_id : ${article.id}")
                    startActivity(intent)
                }

                // apply
                binding.articlesContainer.addView(itemBinding.root)
                lastAddedViewId = itemBinding.root.id
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}