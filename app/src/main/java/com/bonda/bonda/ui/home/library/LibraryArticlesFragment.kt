package com.bonda.bonda.ui.home.library

import android.content.Intent
import android.graphics.Rect
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
import androidx.recyclerview.widget.RecyclerView
import com.bonda.bonda.databinding.FragmentHomeLibraryArticlesBinding
import com.bonda.bonda.model.toSortOrder
import com.bonda.bonda.ui.article.ArticleActivity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LibraryArticlesFragment : Fragment() {

    private var _binding: FragmentHomeLibraryArticlesBinding? = null
    private val binding get() = _binding!!

    private val vm: LibraryViewModel by viewModels({ requireParentFragment() })
    private lateinit var articlesAdapter: SavedArticlePagingAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeLibraryArticlesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        articlesAdapter = SavedArticlePagingAdapter { article ->
            val intent = Intent(requireContext(), ArticleActivity::class.java)
            intent.putExtra("article_detail_id", article.articleId)
            startActivity(intent)
        }

        binding.rv.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rv.adapter = articlesAdapter

        /**
         * dp 값을 pixel 값으로 변환합니다.
         */
        fun Int.dpToPx(): Int {
            return (this * resources.displayMetrics.density).toInt()
        }

        /**
         * RecyclerView 아이템 간 상하 10dp, 좌우 16dp의 간격을 추가합니다.
         */
        binding.rv.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(
                outRect: Rect,
                view: View,
                parent: RecyclerView,
                state: RecyclerView.State
            ) {
                super.getItemOffsets(outRect, view, parent, state)
                outRect.left = 5.dpToPx()
                outRect.right = 5.dpToPx()

                outRect.top = 8.dpToPx()
                outRect.bottom = 8.dpToPx()
            }
        })

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