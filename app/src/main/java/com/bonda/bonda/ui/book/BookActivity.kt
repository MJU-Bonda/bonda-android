package com.bonda.bonda.ui.book

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import coil3.load
import com.bonda.bonda.R
import com.bonda.bonda.databinding.ActivityBookDetailBinding
import com.bonda.bonda.databinding.ViewArticleMiniBinding
import com.bonda.bonda.databinding.ViewChipBookCategoryBinding
import com.bonda.bonda.model.ArticleCategory
import com.bonda.bonda.model.toArticleCategory
import com.bonda.bonda.model.toBookCategory
import com.bonda.bonda.model.toBookTheme
import com.bonda.bonda.ui.article.ArticleActivity
import com.bonda.bonda.ui.profile.activity.MyActivityActivity
import com.bonda.bonda.util.SnackbarType
import com.bonda.bonda.util.showSnackbar
import kotlinx.coroutines.launch

class BookActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBookDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityBookDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bookId = intent.getLongExtra("book_detail_id", 0)
        Log.d("DEBUG", "started_book_detail_activity_id : $bookId")

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }


        val vm = ViewModelProvider(this)[BookViewModel::class.java]
        vm.getBookDetail(bookId)

        vm.title.observe(this) { binding.toolbar.title = it }

        vm.isSaved.observe(this) { isSaved ->
            binding.bookmarkButton.apply {
                if (isSaved) setImageResource(R.drawable.ic_action_bookmark_fill_24dp)
                else setImageResource(R.drawable.ic_action_bookmark_empty_24dp)
                setOnClickListener {
                    lifecycleScope.launch {
                        try {
                            // TODO 스낵바 버그 있음

                            val hasNewBadge = vm.toggleSaveBook(bookId)

                            showSnackbar(
                                message = "도서 저장이 완료되었습니다!",
                                buttonText = "서재로 이동",
                                onButtonClick = {
                                    // TODO 서재로 이동 로직 구현
                                },
                                type = SnackbarType.SAVE
                            )

                            if (hasNewBadge)
                                showSnackbar(
                                    message = "새로운 뱃지를 획득했습니다!",
                                    buttonText = "확인하기",
                                    onButtonClick = {
                                        val intent = Intent(
                                            this@BookActivity,
                                            MyActivityActivity::class.java
                                        )
                                        startActivity(intent)
                                    },
                                    type = SnackbarType.BADGE
                                )
                        } catch (e: Exception) {
                            showSnackbar(
                                message = "저장에 실패했어요. 다시 시도해 주세요.",
                                type = SnackbarType.ERROR
                            )
                        }
                    }
                }
            }
        }

        /**
         * 도서 정보를 binding 합니다
         */
        vm.coverImage.observe(this) { binding.coverImage.load(it) }
        vm.category.observe(this) { binding.bookCategory.root.text = it.toBookCategory().label }
        vm.title.observe(this) { binding.title.text = it }
        vm.author.observe(this) { binding.author.text = it }
        vm.publisher.observe(this) { binding.bookPublisher.text = it }
        vm.size.observe(this) { binding.bookSize.text = it }
        vm.pageLength.observe(this) { binding.bookPageLength.text = it.toString() }
        vm.theme.observe(this) { theme ->
            if (theme.isNullOrBlank()) {
                binding.bookTheme.root.visibility = View.GONE
                binding.bookThemeHeader.visibility = View.GONE
            } else {
                binding.bookTheme.root.text = theme.toBookTheme().label
            }
        }

        vm.body.observe(this) { body ->
            if (body.isNullOrBlank()) {
                binding.bookBodyHeader.visibility = View.GONE
                binding.body.visibility = View.GONE
            } else {
                binding.body.text = body
            }
        }

        /**
         * 연관된 article 목록 표시
         */
        vm.articles.observe(this) { articles ->
            binding.bookArticlesContainer.removeAllViews()

            var lastAddedViewId: Int? = null

            if (articles.isNullOrEmpty()) {
                binding.bookArticlesHeader.visibility = View.INVISIBLE
                binding.bookArticlesContainer.visibility = View.INVISIBLE
            } else {
                articles.forEach { article ->
                    val itemBinding = ViewArticleMiniBinding.inflate(
                        layoutInflater,
                        binding.bookArticlesContainer,
                        false
                    )

                    itemBinding.root.id = View.generateViewId()

                    itemBinding.articleImage.load(article.coverImage)
                    itemBinding.articleTitle.text = article.title

                    article.category.also {
                        val category = it.toArticleCategory()

                        itemBinding.articleCategory.root.text = category.label

                        val bgColorRes = when (category) {
                            ArticleCategory.AUTHOR_OR_PUBLISHER -> R.color.surface_context_writer
                            ArticleCategory.BOOKSTORE -> R.color.surface_context_store
                            ArticleCategory.THEME -> R.color.surface_context_theme
                            else -> R.color.surface_default_primary
                        }
                        val textColorRes = when (category) {
                            ArticleCategory.AUTHOR_OR_PUBLISHER -> R.color.text_context_writer
                            ArticleCategory.BOOKSTORE -> R.color.text_context_store
                            ArticleCategory.THEME -> R.color.text_context_theme
                            else -> R.color.text_accent_primary
                        }

                        // Chip 에 적용
                        itemBinding.articleCategory.root.chipBackgroundColor =
                            ColorStateList.valueOf(
                                ContextCompat.getColor(this, bgColorRes)
                            )
                        itemBinding.articleCategory.root.setTextColor(
                            ContextCompat.getColor(this, textColorRes)
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
}