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
import com.bonda.bonda.ui.book.BookActivity
import com.bonda.bonda.ui.components.BaseFragment

class SearchResultBookFragment : BaseFragment() {

    companion object {
        fun newInstance(): SearchResultBookFragment = SearchResultBookFragment()
    }

    private var _binding: FragmentSearchResultBinding? = null
    private val binding get() = _binding!!
    private val vm: SearchViewModel by activityViewModels()
    private lateinit var bookAdapter: BookAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding = FragmentSearchResultBinding.inflate(layoutInflater)
        setBaseContent(binding.root)

        setupRecyclerView()

        /**
         * 로딩 상태를 관찰해서 화면에 반영합니다
         */
        vm.isLoading.observe(viewLifecycleOwner) { isLoading ->
            /**
             * 페이지네이션 중에는 전체 화면 로딩을 표시하지 않음
             */
            val isPaginating = bookAdapter.itemCount > 0
            if (!isPaginating) {
                showLoadingView(isLoading)
                binding.root.visibility = if (isLoading) View.INVISIBLE else View.VISIBLE
            }
        }

        /**
         * 오류 상태를 관찰해서 화면에 반영합니다
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
         * 도서 검색 결과 개수 바인딩
         */
        vm.booksSearchResultCount.observe(viewLifecycleOwner) { count ->
            binding.tvSearchResultCount.text = count.toString()
        }

        /**
         * 도서 검색 결과 바인딩
         */
        vm.booksSearchResult.observe(viewLifecycleOwner) { books ->
            bookAdapter.submitList(books.toList())

            val keyword = vm.searchKeyword
            if (books.isEmpty() && keyword.isNotEmpty()) {
                binding.noSearchResult.visibility = View.VISIBLE

                val boldKeyword = "'$keyword'"
                val normalText = "에 대한\n도서 검색 결과가 없습니다"
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
     * 오류 페이지에서 재시도 버튼을 클릭했을 때
     */
    override fun onRetry() {
        vm.search(vm.searchKeyword)
    }

    private fun setupRecyclerView() {
        bookAdapter = BookAdapter { book ->
            val intent = Intent(requireContext(), BookActivity::class.java)
            intent.putExtra("book_detail_id", book.id)
            startActivity(intent)
        }

        val gridLayoutManager = GridLayoutManager(requireContext(), 3)

        /**
         * item 사이 gap을 추가합니다
         */
        val horizontalSpacing = 12.dpToPx()
        val verticalSpacing = 24.dpToPx()

        binding.rv.apply {
            adapter = bookAdapter
            layoutManager = gridLayoutManager

            addItemDecoration(
                GridSpacingItemDecoration(3, horizontalSpacing, verticalSpacing)
            )

            /**
             * 화면의 아래까지 스크롤되면 다음 페이지를 로드합니다
             */
            addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    val lastVisibleItemPosition =
                        gridLayoutManager.findLastCompletelyVisibleItemPosition()
                    val totalItemCount = bookAdapter.itemCount

                    /**
                     * 다음 페이지가 있으면 로드
                     */
                    if (lastVisibleItemPosition == totalItemCount - 1 && vm.booksHasNextPage.value == true) {
                        vm.getNextBooks()
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