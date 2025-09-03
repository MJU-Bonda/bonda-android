package com.bonda.bonda.ui.search

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bonda.bonda.databinding.FragmentSearchResultBinding
import com.bonda.bonda.model.GridSpacingItemDecoration
import com.bonda.bonda.model.dpToPx
import com.bonda.bonda.ui.article.ArticleActivity
import com.bonda.bonda.ui.components.BaseFragment

class SearchResultArticleFragment : BaseFragment() {

    companion object {
        fun newInstance(): SearchResultArticleFragment = SearchResultArticleFragment()
    }

    private var _binding: FragmentSearchResultBinding? = null
    private val binding get() = _binding!!
    private val vm: SearchViewModel by activityViewModels()
    private lateinit var articleAdapter: ArticleAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentSearchResultBinding.inflate(layoutInflater)
        setBaseContent(binding.root)

        setupRecyclerView()

        /**
         * 로딩 상태를 관찰합니다
         */
        vm.isLoading.observe(viewLifecycleOwner) { isLoading ->
            // 페이지네이션 중에는 전체 화면 로딩을 표시하지 않음
            val isPaginating = articleAdapter.itemCount > 0
            if (!isPaginating) {
                showLoadingView(isLoading)
                binding.root.visibility = if (isLoading) View.INVISIBLE else View.VISIBLE
            }
        }

        /**
         * 에러 상태를 관찰합니다
         */
        vm.isError.observe(viewLifecycleOwner) { isError ->
            showErrorView(isError)
            if (isError) {
                binding.root.visibility = View.INVISIBLE
            }
        }

        /**
         * 정렬 토글 버튼 클릭 리스너
         */
        binding.btSort.setOnClickListener { vm.toggleSortOrderAndSearch() }
        vm.sortOrder.observe(viewLifecycleOwner) { binding.tvSortIndicator.text = it.label }

        /**
         * 아티클 검색 결과 개수 바인딩
         */
        vm.articlesSearchResultCount.observe(viewLifecycleOwner) { count ->
            binding.tvSearchResultCount.text = count.toString()
        }

        /**
         * 아티클 검색 결과 바인딩
         */
        vm.articlesSearchResult.observe(viewLifecycleOwner) { articles ->
            articleAdapter.submitList(articles.toList())

            val keyword = vm.searchKeyword
            if (articles.isEmpty() && keyword.isNotEmpty()) {
                binding.noSearchResult.visibility = View.VISIBLE

                val boldKeyword = "'$keyword'"
                val normalText = "에 대한\n아티클 검색 결과가 없습니다"
                val fullText = boldKeyword + normalText

                val spannable = SpannableStringBuilder(fullText)
                spannable.setSpan(
                    StyleSpan(Typeface.BOLD),
                    0,
                    boldKeyword.length,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                binding.tvNoResult.text = spannable
            } else {
                binding.noSearchResult.visibility = View.GONE
            }
        }
    }

    /**
     * 오류화면에서 재시도 버튼을 클릭했을 때
     */
    override fun onRetry() {
        vm.search(vm.searchKeyword)
    }

    /**
     * RecyclerView를 초기화합니다
     */
    private fun setupRecyclerView() {
        articleAdapter = ArticleAdapter { article ->
            val intent = Intent(requireContext(), ArticleActivity::class.java)
            intent.putExtra("article_detail_id", article.id)
            startActivity(intent)
        }

        val gridLayoutManager = GridLayoutManager(requireContext(), 2)

        /**
         * item 사이 gap을 추가합니다
         */
        val horizontalSpacing = 10.dpToPx()
        val verticalSpacing = 16.dpToPx()

        binding.rv.apply {
            adapter = articleAdapter
            layoutManager = gridLayoutManager

            addItemDecoration(
                GridSpacingItemDecoration(2, horizontalSpacing, verticalSpacing)
            )

            /**
             * 화면의 아래에 닿으면 다음 페이지를 로드합니다
             */
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    val lastVisibleItemPosition =
                        gridLayoutManager.findLastCompletelyVisibleItemPosition()
                    val totalItemCount = articleAdapter.itemCount

                    /**
                     * 마지막 아이템이 보이고, 다음 페이지가 존재하면 다음 페이지 로드
                     */
                    if (lastVisibleItemPosition == totalItemCount - 1 && vm.articlesHasNextPage.value == true) {
                        vm.getNextArticles()
                    }
                }
            })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}