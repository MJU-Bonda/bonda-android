package com.bonda.bonda.ui.book

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import coil3.load
import com.bonda.bonda.model.AppEvents
import com.bonda.bonda.R
import com.bonda.bonda.databinding.ActivityBookDetailBinding
import com.bonda.bonda.databinding.ViewArticleMiniBinding
import com.bonda.bonda.model.ArticleCategory
import com.bonda.bonda.model.toArticleCategory
import com.bonda.bonda.model.toBookCategory
import com.bonda.bonda.model.toBookTheme
import com.bonda.bonda.ui.article.ArticleActivity
import com.bonda.bonda.ui.home.HomeActivity
import com.bonda.bonda.ui.profile.activity.MyActivityActivity
import com.bonda.bonda.ui.components.SnackbarType
import com.bonda.bonda.model.TAG
import com.bonda.bonda.ui.components.showSnackbar
import kotlinx.coroutines.launch

class BookActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBookDetailBinding
    private val vm: BookViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityBookDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val bookId = intent.getLongExtra("book_detail_id", 0)
        vm.getBookDetail(bookId)

        /**
         * 상단 앱바 처리
         */
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener { onBackPressedDispatcher.onBackPressed() }
        vm.title.observe(this) { binding.toolbar.title = it }

        /**
         * 에러 페이지 처리
         */
        binding.errorNetwork.buttonRetry.setOnClickListener { vm.getBookDetail(bookId) }
        vm.isError.observe(this) { binding.errorNetwork.root.isVisible = it }

        /**
         * 도서 북마크 버튼 처리
         */
        vm.isSaved.observe(this) { isSaved ->
            binding.bookmarkButton.setImageResource(
                if (isSaved) R.drawable.ic_action_bookmark_fill_24dp
                else R.drawable.ic_action_bookmark_empty_24dp
            )
            binding.bookmarkButton.setOnClickListener {
                lifecycleScope.launch {
                    try {
                        val hasNewBadge = vm.toggleSaveBook(bookId)

                        /**
                         * 도서 저장 완료시
                         */
                        if (!isSaved)
                            showSnackbar(
                                message = "도서 저장이 완료되었습니다!",
                                buttonText = "서재로 이동",
                                onButtonClick = {
                                    val intent = Intent(this@BookActivity, HomeActivity::class.java)
                                    intent.putExtra("navDest", "library")
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                                    startActivity(intent)
                                    finish()
                                },
                                type = SnackbarType.SAVE
                            )

                        AppEvents.profileUpdated.emit(Unit)

                        /**
                         * 새로운 뱃지 획득시
                         */
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
                        /**
                         * 오류 발생시
                         */
                        showSnackbar(
                            message = "저장에 실패했어요. 다시 시도해 주세요.",
                            type = SnackbarType.ERROR
                        )
                        Log.e(
                            TAG,
                            "BookActivity.kt::onCreate::binding.bookmarkButton.setOnClickListener",
                            e
                        )
                    }
                }
            }
        }

        /**
         * 도서 데이터 바인딩
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
                binding.bookTheme.visibility = View.GONE
                binding.bookThemeHeader.visibility = View.GONE
            } else {
                binding.bookTheme.text = theme.toBookTheme().label
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
         * 연관된 아티클 목록 표시
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

                    val params = itemBinding.root.layoutParams as ConstraintLayout.LayoutParams

                    if (lastAddedViewId != null)
                        params.topToBottom = lastAddedViewId!!
                    else
                        params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID

                    itemBinding.root.layoutParams = params

                    /**
                     * 아티클 클릭 시 아티클 상세조회 액티비티를 시작합니다
                     */
                    itemBinding.root.setOnClickListener {
                        val intent = Intent(this, ArticleActivity::class.java)
                        intent.putExtra("article_detail_id", article.id)
                        startActivity(intent)
                    }

                    binding.bookArticlesContainer.addView(itemBinding.root)
                    lastAddedViewId = itemBinding.root.id
                }
            }
        }
    }

}