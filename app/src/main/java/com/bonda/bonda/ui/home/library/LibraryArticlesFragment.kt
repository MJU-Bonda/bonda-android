package com.bonda.bonda.ui.home.library

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.paging.LoadState
import androidx.recyclerview.widget.GridLayoutManager
import com.bonda.bonda.databinding.FragmentHomeLibraryArticlesBinding
import com.bonda.bonda.model.GridSpacingItemDecoration
import com.bonda.bonda.model.toSortOrder
import com.bonda.bonda.ui.article.ArticleActivity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch


class LibraryArticlesFragment : Fragment() {

    private var _binding: FragmentHomeLibraryArticlesBinding? = null // 변경된 부분
    private val binding get() = _binding!!

    private val vm: LibraryViewModel by viewModels({ requireParentFragment() })
    private lateinit var articlesAdapter: SavedArticlePagingAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeLibraryArticlesBinding.inflate(inflater, container, false) // 변경된 부분
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 아티클 목록 표시 로직만 남김
        articlesAdapter = SavedArticlePagingAdapter { article ->
            val intent = Intent(requireContext(), ArticleActivity::class.java)
            intent.putExtra("article_detail_id", article.articleId)
            startActivity(intent)
        }

        /**
         * 아티클 사이 상하좌우 gap을 설정합니다
         */
        fun dpToPx(dp: Int): Int {
            return (dp * resources.displayMetrics.density).toInt()
        }

        val spanCount = 2
        val horizontalSpacing = dpToPx(10)
        val verticalSpacing = dpToPx(16)

        binding.rv.layoutManager = GridLayoutManager(requireContext(), spanCount)

        if (binding.rv.itemDecorationCount > 0) {
            binding.rv.removeItemDecorationAt(0)
        }
        binding.rv.addItemDecoration(GridSpacingItemDecoration(spanCount, horizontalSpacing, verticalSpacing))

        binding.rv.adapter = articlesAdapter

        /**
         * 로딩 상태에 따라 빈 목록 뷰(tv_empty)의 노출 여부를 결정합니다.
         * Paging refresh가 끝나고(NotLoading) 아이템 개수가 0이면 빈 화면을 표시합니다.
         */
        viewLifecycleOwner.lifecycleScope.launch {
            articlesAdapter.loadStateFlow.collectLatest { loadStates ->
                val isListEmpty = loadStates.refresh is LoadState.NotLoading && articlesAdapter.itemCount == 0
                binding.tvEmpty.isVisible = isListEmpty
                binding.rv.isVisible = !isListEmpty
            }
        }

        /**
         * 저장한 아티클 갯수를 화면에 표시합니다
         */
        vm.savedArticleCount.observe(viewLifecycleOwner) {
            if (it < 1000) binding.tvItemCount.text = it.toString()
            else binding.tvItemCount.text = "999+"
        }

        /**
         *
         */
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                vm.savedArticlesFlow.collectLatest { articlesAdapter.submitData(it) }
            }
        }

        /**
         *
         */
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                vm.articleSortOrder.collect {
                    binding.textSortIndicator.text = it.toSortOrder().label
                }
            }
        }
        binding.btSort.setOnClickListener { vm.toggleArticleSortOrder() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}