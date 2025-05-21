package com.bonda.bonda.ui.detail.book

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
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
        enableEdgeToEdge()

        binding = ActivityBookBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bookId = intent.getIntExtra("book_detail_id", 0)
        Log.d("DEBUG", "started_book_detail_activity_id : $bookId")

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        supportActionBar?.apply {
            title = "BONDA"
            setDisplayHomeAsUpEnabled(true)
        }

        val bookViewModel = ViewModelProvider(this)[BookViewModel::class.java]

        // TODO: isSaved binding 코드 추가
        bookViewModel.coverImage.observe(this) { binding.coverImage.setImageResource(it) }
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
        bookViewModel.title.observe(this) { binding.title.text = it }
        bookViewModel.author.observe(this) { binding.author.text = it }
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
        bookViewModel.body.observe(this) { binding.body.text = it }
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

                // start new article detail activity
                itemBinding.root.setOnClickListener {
                    val intent = Intent(this, ArticleActivity::class.java)
                    intent.putExtra("article_detail_id", article.id)
                    Log.d("DEBUG", "start_article_detail_activity_id : ${article.id}")
                    startActivity(intent)
                }

                // apply
                binding.bookArticlesContainer.addView(itemBinding.root)
                lastAddedViewId = itemBinding.root.id
            }
        }
    }
}