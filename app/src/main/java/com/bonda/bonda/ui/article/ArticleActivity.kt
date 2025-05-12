package com.bonda.bonda.ui.article

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import com.bonda.bonda.R
import com.bonda.bonda.databinding.ActivityArticleBinding
import com.bonda.bonda.databinding.ViewArticleMiniBinding
import com.bonda.bonda.databinding.ViewChipPublisherBinding
import com.bonda.bonda.databinding.ViewChipThemeBinding

class ArticleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityArticleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityArticleBinding.inflate(layoutInflater)
        setContentView(binding.root)
        enableEdgeToEdge()

        supportActionBar?.apply {
            title = "BONDA"
            setDisplayHomeAsUpEnabled(true)
        }

        // view-model apply
        val articleViewModel = ViewModelProvider(this).get(ArticleViewModel::class.java)

        articleViewModel.title.observe(this) { binding.articleTitle.text = it }
        articleViewModel.subTitle.observe(this) { binding.articleSubtitle.text = it }
        articleViewModel.body.observe(this) { binding.articleBody.text = it }
        articleViewModel.coverImage.observe(this) {binding.articleImage.setImageResource(it) }

        articleViewModel.category.observe(this) { category ->
            binding.articleCategoryChipGroup.removeAllViews()

            when (category) {
                "테마" -> ViewChipThemeBinding.inflate(
                    layoutInflater,
                    binding.articleCategoryChipGroup,
                    true
                )
                "작가/출판사" -> ViewChipThemeBinding.inflate(
                    layoutInflater,
                    binding.articleCategoryChipGroup,
                    true
                )
            }
        }

        articleViewModel.isSaved.observe(this) { isSaved ->
            if (isSaved) {
                binding.articleButtonBookmark.setImageResource(R.drawable.ic_bookmark_saved_36dp)
            } else {
                binding.articleButtonBookmark.setImageResource((R.drawable.ic_bookmark_36dp))
            }
        }

        articleViewModel.articles.observe(this) { list ->
            binding.articlesContainer.removeAllViews()

            var lastAddedViewId: Int? = null

            list.forEach { article ->
                val itemBinding = ViewArticleMiniBinding.inflate(
                    layoutInflater,
                    binding.articlesContainer,
                    false
                )

                itemBinding.root.id = View.generateViewId()

                // view-model binding
                itemBinding.articleImage.setImageResource(article.coverImage)
                itemBinding.articleTitle.text = article.title

                if (article.category == "테마") {
                    ViewChipThemeBinding.inflate(
                        layoutInflater,
                        itemBinding.articleCategoryChipGroup,
                        true
                    )
                } else if (article.category == "작가/출판사") {
                    ViewChipPublisherBinding.inflate(
                        layoutInflater,
                        itemBinding.articleCategoryChipGroup,
                        true
                    )
                }

                // set layout constraint
                val params = itemBinding.root.layoutParams as ConstraintLayout.LayoutParams

                if (lastAddedViewId != null) {
                    params.topToBottom = lastAddedViewId!!
                } else {
                    params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                }

                itemBinding.root.layoutParams = params

                // TODO: onclick binding logic
                itemBinding.root.setOnClickListener {
                    val intent = Intent(this, ArticleActivity::class.java)
                    startActivity(intent)
                }

                // apply
                binding.articlesContainer.addView(itemBinding.root)
                lastAddedViewId = itemBinding.root.id
            }
        }
    }
}