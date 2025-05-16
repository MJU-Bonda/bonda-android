package com.bonda.bonda.ui.main.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bonda.bonda.R
import com.bonda.bonda.databinding.FragmentHomeBinding
import com.bonda.bonda.databinding.ViewArticleBinding
import com.bonda.bonda.databinding.ViewChipPublisherBinding
import com.bonda.bonda.databinding.ViewChipThemeBinding
import com.bonda.bonda.ui.detail.article.ArticleActivity

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        homeViewModel.articles.observe(viewLifecycleOwner) { list ->
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
                itemBinding.articleImage.setImageResource(article.coverImage)
                itemBinding.articleTitle.text = article.title
                itemBinding.articleSubtitle.text = article.subTitle

                if (article.category == "테마") {
                    ViewChipThemeBinding.inflate(
                        layoutInflater,
                        itemBinding.articleChipGroup,
                        true
                    )
                } else if (article.category == "작가/출판사") {
                    ViewChipPublisherBinding.inflate(
                        layoutInflater,
                        itemBinding.articleChipGroup,
                        true
                    )
                }

                if (article.isSaved) {
                    itemBinding.articleButtonBookmark.setImageResource(R.drawable.ic_bookmark_saved_36dp)
                } else {
                    itemBinding.articleButtonBookmark.setImageResource((R.drawable.ic_bookmark_36dp))
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

                // TODO: onclick binding logic
                itemBinding.root.setOnClickListener {
                    val intent = Intent(requireContext(), ArticleActivity::class.java)
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