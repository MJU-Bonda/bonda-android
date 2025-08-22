package com.bonda.bonda.ui.home.articles

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import coil3.load
import com.bonda.bonda.AppEvents
import com.bonda.bonda.R
import com.bonda.bonda.databinding.FragmentHomeArticlesBinding
import com.bonda.bonda.databinding.ViewArticleBinding
import com.bonda.bonda.model.ArticleCategory
import com.bonda.bonda.model.toArticleCategory
import com.bonda.bonda.ui.article.ArticleActivity
import com.bonda.bonda.ui.home.HomeActivity
import com.bonda.bonda.ui.profile.activity.MyActivityActivity
import com.bonda.bonda.ui.search.SearchActivity
import com.bonda.bonda.util.SnackbarType
import com.bonda.bonda.util.showSnackbar
import kotlinx.coroutines.launch

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

        val vm = ViewModelProvider(this)[ArticlesViewModel::class.java]

        vm.articles.observe(viewLifecycleOwner) { list ->
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
                // TODO 이미지 로드 오류 처리 필요
                itemBinding.articleImage.load(article.coverImage)
                itemBinding.articleTitle.text = article.title.replace("\\n", "\n")
                itemBinding.articleSubtitle.text = article.subTitle

                article.category.also {
                    val category = it.toArticleCategory()

                    itemBinding.articleCategoryChip.root.text = category.label

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
                    itemBinding.articleCategoryChip.root.chipBackgroundColor =
                        ColorStateList.valueOf(
                            ContextCompat.getColor(requireContext(), bgColorRes)
                        )
                    itemBinding.articleCategoryChip.root.setTextColor(
                        ContextCompat.getColor(
                            requireContext(),
                            textColorRes
                        )
                    )
                }

                /**
                 * 북마크 버튼 binding
                 */
                itemBinding.articleButtonBookmark.setImageResource(
                    if (article.isSaved) R.drawable.ic_action_bookmark_fill_24dp
                    else R.drawable.ic_action_bookmark_empty_24dp
                )
                itemBinding.articleButtonBookmark.setOnClickListener {
                    lifecycleScope.launch {
                        try {
                            val hasNewBadge = vm.toggleSaved(article.id)

                            /**
                             * 아티클 저장 완료시
                             */
                            if (!article.isSaved)
                                (requireActivity() as AppCompatActivity)
                                    .showSnackbar(
                                        message = "아티클 저장이 완료되었습니다!",
                                        buttonText = "서재로 이동",
                                        onButtonClick = {
                                            val intent =
                                                Intent(requireContext(), HomeActivity::class.java)
                                            intent.putExtra("navDest", "library")
                                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                                            startActivity(intent)
                                        },
                                        type = SnackbarType.SAVE
                                    )

                            /**
                             * 새로운 뱃지 획득시
                             */
                            if (hasNewBadge) {
                                (requireActivity() as AppCompatActivity)
                                    .showSnackbar(
                                        message = "새로운 뱃지를 획득했습니다!",
                                        buttonText = "확인하기",
                                        onButtonClick = {
                                            val intent = Intent(
                                                requireContext(),
                                                MyActivityActivity::class.java
                                            )
                                            startActivity(intent)
                                        },
                                        type = SnackbarType.BADGE
                                    )
                                AppEvents.profileUpdated.emit(Unit)
                            }
                        } catch (e: Exception) {
                            /**
                             * 오류 발생시
                             */
                            (requireActivity() as AppCompatActivity)
                                .showSnackbar(
                                    message = "저장에 실패했어요. 다시 시도해 주세요.",
                                    type = SnackbarType.ERROR
                                )
                        }

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