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
import androidx.viewpager2.widget.ViewPager2
import coil3.load
import com.bonda.bonda.model.AppEvents
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
import com.bonda.bonda.ui.components.SnackbarType
import com.bonda.bonda.model.TAG
import com.bonda.bonda.model.setCategoryStyle
import com.bonda.bonda.ui.components.showSnackbar
import kotlinx.coroutines.launch

class ArticleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityArticleDetailBinding
    private val vm: ArticleViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityArticleDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /**
         * display inset을 전달합니다
         */
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        /**
         * activity 실행 시 전달받은 article id를 받아옵니다
         */
        val articleId = intent.getLongExtra("article_detail_id", 0)
        vm.getArticleData(articleId)

        /**
         * 만약 뱃지를 받았다면 스낵바를 호출합니다
         */
        vm.hasNewBadge.observe(this) { hasNewBadge ->
            if (hasNewBadge) {
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
                lifecycleScope.launch { AppEvents.profileUpdated.emit(Unit) }
            }
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
            binding.errorNetwork.root.isVisible = it
            binding.scrollView.isGone = it
        }

        binding.errorNetwork.buttonRetry.setOnClickListener { vm.getArticleData(articleId) }

        /**
         * 데이터 바인딩
         */
        vm.title.observe(this) { binding.titleTv.text = it.replace("\\n", "\n") }
        vm.subTitle.observe(this) { binding.subtitleTv.text = it }
        vm.body.observe(this) { binding.articleBody.text = it }
        vm.coverImage.observe(this) { imageUrl ->
            binding.articleImage.load(imageUrl) {
                listener(
                    onSuccess = { _, _ ->
                        binding.articleImageGradient.isVisible = true
                    },
                    onError = { _, result ->
                        Log.e(TAG, "ArticleActivity::coverImage", result.throwable)
                        vm.setErrorState(true)
                    }
                )
            }
        }
        vm.category.observe(this) { binding.categoryChip.root.setCategoryStyle(it) }

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
                                    intent.putExtra("initialTab", "article")
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                                    startActivity(intent)
                                },
                                type = SnackbarType.SAVE
                            )

                        /**
                         * 새로운 뱃지 획득시
                         */
                        if (hasNewBadge) {
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
                            AppEvents.profileUpdated.emit(Unit)
                        }

                        AppEvents.homeArticlesUpdated.emit(Unit)
                    } catch (e: Exception) {
                        /**
                         * 오류 발생시
                         */
                        showSnackbar(
                            message = "저장에 실패했어요. 다시 시도해 주세요.",
                            type = SnackbarType.ERROR
                        )
                        Log.e(TAG, "ArticleActivity.kt::bookmarkButton", e)
                    }
                }
            }
        }

        /**
         * 도서 카드 목록 도서 데이터 바인딩
         */
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

        /**
         * 도서 카드 목록 탭 인디케이터
         */
        vm.books.observe(this) { binding.booksTabIndicator.setCount(it.size) }
        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                binding.booksTabIndicator.select(position)
            }
        })

        /**
         * 그리드 컨테이너 도서 목록 바인딩
         */
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
                    startActivity(intent)
                }

                binding.booksGridContainer.addView(itemBinding.root)
            }
        }

        /**
         * 다른 아티클 목록 바인딩
         */
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

                    itemBinding.articleCategory.root.chipBackgroundColor =
                        ColorStateList.valueOf(
                            ContextCompat.getColor(this, bgColorRes)
                        )
                    itemBinding.articleCategory.root.setTextColor(
                        ContextCompat.getColor(this, textColorRes)
                    )
                }

                /**
                 * layout constraint parameters를 설정합니다
                 */
                val params = itemBinding.root.layoutParams as ConstraintLayout.LayoutParams
                if (lastAddedViewId != null) {
                    params.topToBottom = lastAddedViewId!!
                } else {
                    params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
                }

                itemBinding.root.layoutParams = params

                /**
                 * 클릭 시 새로운 아티클 상세보기 activity를 실행합니다
                 */
                itemBinding.root.setOnClickListener {
                    val intent = Intent(this, ArticleActivity::class.java)
                    intent.putExtra("article_detail_id", article.id)
                    startActivity(intent)
                }

                binding.articlesContainer.addView(itemBinding.root)
                lastAddedViewId = itemBinding.root.id
            }
        }
    }
}