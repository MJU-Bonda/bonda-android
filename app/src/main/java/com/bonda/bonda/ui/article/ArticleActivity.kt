package com.bonda.bonda.ui.article

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.GridLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bonda.bonda.R
import com.bonda.bonda.databinding.ActivityArticleDetailBinding
import com.bonda.bonda.databinding.ViewArticleMiniBinding
import com.bonda.bonda.databinding.ViewBookVerticalBinding
import com.bonda.bonda.databinding.ViewChipWriterBinding
import com.bonda.bonda.databinding.ViewChipThemeBinding
import com.bonda.bonda.ui.book.BookActivity

class ArticleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityArticleDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityArticleDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val articleId = intent.getIntExtra("article_detail_id", 0)
        Log.d("DEBUG", "started_article_detail_activity_id : $articleId")

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // view-model apply
        val articleViewModel = ViewModelProvider(this)[ArticleViewModel::class.java]

        articleViewModel.title.observe(this) { binding.articleTitle.text = it }
        articleViewModel.subTitle.observe(this) { binding.articleSubtitle.text = it }
        articleViewModel.body.observe(this) { binding.articleBody.text = it }
        articleViewModel.coverImage.observe(this) {binding.articleImage.setImageResource(it) }

        articleViewModel.category.observe(this) { category ->
            binding.articleCategoryChipGroup.removeAllViews()

            // TODO: binding 로직 수정
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
                binding.articleButtonBookmark.setImageResource(R.drawable.ic_action_bookmark_fill_24dp)
            } else {
                binding.articleButtonBookmark.setImageResource((R.drawable.ic_action_bookmark_empty_24dp))
            }
        }

        // 도서 목록 binding 1
        articleViewModel.books.observe(this) { books ->
            val fragments = books.mapIndexed { index, book ->
                BookCardFragment.newInstance(
                    index,
                    book.id,
                    book.coverImage,
                    book.category,
                    book.title,
                    book.author,
                    book.body
                )
            }

            binding.viewPager.adapter = object: FragmentStateAdapter(this@ArticleActivity) {
                override fun getItemCount(): Int = fragments.size
                override fun createFragment(position: Int): Fragment = fragments[position]
            }
        }

        // 도서 목록 binding 2
        articleViewModel.books.observe(this) { books ->
            binding.booksGridContainer.removeAllViews()

            books.forEach { book ->
                val itemBinding = ViewBookVerticalBinding.inflate(
                    layoutInflater,
                    binding.booksGridContainer,
                    false
                )

                itemBinding.coverImage.setImageResource(book.coverImage)
                itemBinding.title.text = book.title
                itemBinding.author.text = book.author
                itemBinding.category.root.text = book.category

                val params = itemBinding.root.layoutParams as GridLayout.LayoutParams
                params.width = 0
                params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                itemBinding.root.layoutParams = params

                itemBinding.root.setOnClickListener {
                    val intent = Intent(this, BookActivity::class.java)
                    intent.putExtra("book_detail_id", book.id)
                    Log.d("DEBUG", "start_book_detail_activity_id : ${book.id}")
                    startActivity(intent)
                }

                binding.booksGridContainer.addView(itemBinding.root)
            }
        }

        // 다른 articles 목록 binding
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


                // TODO: chip 로직 수정
                if (article.category == "테마") {
                    ViewChipThemeBinding.inflate(
                        layoutInflater,
                        itemBinding.articleCategoryChipGroup,
                        true
                    )
                } else if (article.category == "작가/출판사") {
                    ViewChipWriterBinding.inflate(
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

                // start article detail activity
                itemBinding.root.setOnClickListener {
                    val intent = Intent(this, ArticleActivity::class.java)
                    intent.putExtra("article_detail_id", article.id)
                    Log.d("DEBUG", "start_article_detail_activity_id : ${article.id}")
                    startActivity(intent)
                }

                // apply
                binding.articlesContainer.addView(itemBinding.root)
                lastAddedViewId = itemBinding.root.id
            }
        }
    }
}