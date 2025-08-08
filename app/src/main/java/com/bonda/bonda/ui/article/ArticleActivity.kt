package com.bonda.bonda.ui.article

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.GridLayout
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.adapter.FragmentStateAdapter
import coil3.load
import com.bonda.bonda.R
import com.bonda.bonda.databinding.ActivityArticleDetailBinding
import com.bonda.bonda.databinding.ViewArticleMiniBinding
import com.bonda.bonda.databinding.ViewBookVerticalBinding
import com.bonda.bonda.model.ArticleCategory
import com.bonda.bonda.model.toArticleCategory
import com.bonda.bonda.model.toBookCategory
import com.bonda.bonda.ui.book.BookActivity
import com.bonda.bonda.ui.home.HomeActivity
import com.bonda.bonda.ui.profile.activity.MyActivityActivity
import com.bonda.bonda.util.SnackbarType
import com.bonda.bonda.util.showSnackbar
import kotlinx.coroutines.launch

class ArticleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityArticleDetailBinding
    private val vm: ArticleViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityArticleDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val articleId = intent.getLongExtra("article_detail_id", 0)
        vm.getArticleData(articleId)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        /**
         * 화면 상단 action-bar binding
         */
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        binding.toolbar.setNavigationOnClickListener { finish() }

        /**
         * 오류 페이지 처리
         */
        vm.isError.observe(this) {
            binding.errorCommon.root.isVisible = it
            binding.scrollView.isGone = it
        }

        binding.errorCommon.buttonRetry.setOnClickListener { vm.getArticleData(articleId) }
        binding.errorNetwork.buttonRetry.setOnClickListener { vm.getArticleData(articleId) }

        /**
         * 데이터 바인딩
         */
        vm.title.observe(this) { binding.titleTv.text = it.replace("\\n", "\n") }
        vm.subTitle.observe(this) { binding.subtitleTv.text = it }
        vm.body.observe(this) { binding.articleBody.text = it }
        vm.coverImage.observe(this) { binding.articleImage.load(it) }

        vm.category.observe(this) {
            val category = it.toArticleCategory()

            binding.categoryChip.root.text = category.label

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
            binding.categoryChip.root.chipBackgroundColor =
                ColorStateList.valueOf(ContextCompat.getColor(this, bgColorRes))
            binding.categoryChip.root.setTextColor(ContextCompat.getColor(this, textColorRes))
        }

        /**
         * 북마크 버튼 binding
         */
        vm.isSaved.observe(this) { isSaved ->
            binding.bookmarkButton.setImageResource(
                if (isSaved) R.drawable.ic_action_bookmark_fill_24dp
                else R.drawable.ic_action_bookmark_empty_24dp
            )

            binding.bookmarkButton.setOnClickListener {
                lifecycleScope.launch {
                    try {
                        val hasNewBadge = vm.toggleSaved()

                        /**
                         * 아티클 저장 완료시
                         */
                        if (!isSaved)
                            showSnackbar(
                                message = "아티클 저장이 완료되었습니다!",
                                buttonText = "서재로 이동",
                                onButtonClick = {
                                    val intent =
                                        Intent(this@ArticleActivity, HomeActivity::class.java)
                                    intent.putExtra("navDest", "library")
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                                    startActivity(intent)
                                },
                                type = SnackbarType.SAVE
                            )

                        /**
                         * 새로운 뱃지 획득시
                         */
                        if (hasNewBadge)
                            showSnackbar(
                                message = "새로운 뱃지를 획득했습니다!",
                                buttonText = "확인하기",
                                onButtonClick = {
                                    val intent = Intent(
                                        this@ArticleActivity,
                                        MyActivityActivity::class.java
                                    )
                                    startActivity(intent)
                                },
                                type = SnackbarType.BADGE
                            )

                    } catch (e: Exception) {
                        /**
                         * 오류 발생시
                         */
                        showSnackbar(
                            message = "저장에 실패했어요. 다시 시도해 주세요.",
                            type = SnackbarType.ERROR
                        )
                    }
                }
            }
        }

        // 도서 목록 binding 1
        vm.books.observe(this) { books ->
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

            binding.viewPager.adapter = object : FragmentStateAdapter(this@ArticleActivity) {
                override fun getItemCount(): Int = fragments.size
                override fun createFragment(position: Int): Fragment = fragments[position]
            }
        }

        // 도서 목록 binding 2
        vm.books.observe(this) { books ->
            binding.booksGridContainer.removeAllViews()

            books.forEach { book ->
                val itemBinding = ViewBookVerticalBinding.inflate(
                    layoutInflater,
                    binding.booksGridContainer,
                    false
                )

                itemBinding.coverImage.load(book.coverImage)
                itemBinding.title.text = book.title
                itemBinding.author.text = book.author
                itemBinding.category.root.text = book.category.toBookCategory().label

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
        vm.articles.observe(this) { list ->
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
                itemBinding.articleImage.load(article.coverImage)
                itemBinding.articleTitle.text = article.title.replace("\\n", " ")

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