package com.bonda.bonda.ui.detail.book

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import com.bonda.bonda.databinding.ActivityBookBinding
import com.bonda.bonda.databinding.ViewArticleMiniBinding
import com.bonda.bonda.databinding.ViewChipBookCategoryBinding
import com.bonda.bonda.databinding.ViewChipPublisherBinding
import com.bonda.bonda.databinding.ViewChipThemeBinding
import com.bonda.bonda.ui.detail.article.ArticleActivity
import com.bonda.bonda.ui.detail.book.BookViewModel.Article

class BookActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBookBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookBinding.inflate(layoutInflater)
        setContentView(binding.root)

        enableEdgeToEdge()

        supportActionBar?.apply {
            title = "BONDA"
            setDisplayHomeAsUpEnabled(true)
        }

        val bookViewModel = ViewModelProvider(this)[BookViewModel::class.java]

//        bookViewModel.isSaved.observe(this) {binding.}
        bookViewModel.coverImage.observe(this) { binding.bookImage.setImageResource(it) }
        bookViewModel.category.observe(this) { category ->
            binding.bookCategoryChipGroup.removeAllViews()

            val itemBinding = ViewChipBookCategoryBinding.inflate(
                layoutInflater,
                binding.bookCategoryChipGroup,
                false
            )

            itemBinding.root.text = category

            binding.bookCategoryChipGroup.addView(itemBinding.root)
        }
        bookViewModel.title.observe(this) { binding.bookTitle.text = it }
        bookViewModel.author.observe(this) { binding.bookAuthor.text = it }
        bookViewModel.publisher.observe(this) { binding.bookPublisher.text = it }
        bookViewModel.size.observe(this) { binding.bookSize.text = it }
        bookViewModel.pageLength.observe(this) { binding.bookPageLength.text = it.toString() }
        bookViewModel.theme.observe(this) { theme ->
            binding.bookThemeChipGroup.removeAllViews()

            val itemBinding = ViewChipBookCategoryBinding.inflate(
                layoutInflater,
                binding.bookThemeChipGroup,
                false
            )

            itemBinding.root.text = theme

            binding.bookThemeChipGroup.addView(itemBinding.root)
        }
        bookViewModel.body.observe(this) { binding.bookBody.text = it }
        bookViewModel.articles.observe(this) { articles ->
            binding.bookArticlesContainer.removeAllViews()

            var lastAddedViewId: Int? = null

            articles.forEach { article ->
                val itemBinding = ViewArticleMiniBinding.inflate(
                    layoutInflater,
                    binding.bookArticlesContainer,
                    false
                )

                itemBinding.root.id = View.generateViewId()

                itemBinding.articleImage.setImageResource(article.coverImage)
                itemBinding.articleTitle.text = article.title

                // TODO: chip 로직 수정
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
                binding.bookArticlesContainer.addView(itemBinding.root)
                lastAddedViewId = itemBinding.root.id
            }
        }
    }
}