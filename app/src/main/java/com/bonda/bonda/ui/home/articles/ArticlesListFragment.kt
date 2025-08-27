package com.bonda.bonda.ui.home.articles

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import coil3.load
import com.bonda.bonda.model.AppEvents
import com.bonda.bonda.R
import com.bonda.bonda.databinding.FragmentHomeArticlesListBinding
import com.bonda.bonda.databinding.ViewArticleBinding
import com.bonda.bonda.model.ArticleCategory
import com.bonda.bonda.model.setCategoryStyle
import com.bonda.bonda.model.toArticleCategory
import com.bonda.bonda.ui.article.ArticleActivity
import com.bonda.bonda.ui.home.HomeActivity
import com.bonda.bonda.ui.profile.activity.MyActivityActivity
import com.bonda.bonda.ui.components.SnackbarType
import com.bonda.bonda.ui.components.showSnackbar
import kotlinx.coroutines.launch

class ArticlesListFragment : Fragment() {

    private var _binding: FragmentHomeArticlesListBinding? = null
    private val binding get() = _binding!!
    private val vm: ArticlesViewModel by activityViewModels()

    companion object {
        private const val ARG_CATEGORY = "arg_category"

        fun newInstance(category: ArticleCategory): ArticlesListFragment {
            val fragment = ArticlesListFragment()
            val args = Bundle().apply {
                putString(ARG_CATEGORY, category.code)
            }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeArticlesListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /**
         * 아티클 데이터 바인딩
         */
        vm.articles.observe(viewLifecycleOwner) { list ->
            binding.articlesContainer.removeAllViews()

            var lastAddedViewId: Int? = null

            list.forEach { article ->
                /**
                 * 필터에 맞는 아티클만 바인딩합니다
                 */
                arguments?.getString(ARG_CATEGORY)?.let {
                    if (it != ArticleCategory.ALL.code && article.category != it)
                        return@forEach
                }

                val itemBinding = ViewArticleBinding.inflate(
                    layoutInflater,
                    binding.articlesContainer,
                    false
                )

                itemBinding.root.id = View.generateViewId()

                itemBinding.articleImage.load(article.coverImage)
                itemBinding.articleTitle.text = article.title.replace("\\n", "\n")
                itemBinding.articleSubtitle.text = article.subTitle
                itemBinding.articleCategoryChip.root.setCategoryStyle(article.category)

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

                            AppEvents.libraryUpdated.emit(Unit)
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

                itemBinding.root.setOnClickListener {
                    val intent = Intent(requireContext(), ArticleActivity::class.java)
                    intent.putExtra("article_detail_id", article.id)
                    startActivity(intent)
                }

                binding.articlesContainer.addView(itemBinding.root)
                lastAddedViewId = itemBinding.root.id
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}