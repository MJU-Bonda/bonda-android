package com.bonda.bonda.ui.search

import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bonda.bonda.databinding.FragmentSearchResultBinding
import com.bonda.bonda.ui.book.BookActivity

class SearchResultBookFragment : Fragment() {

    companion object {
        fun newInstance(): SearchResultBookFragment = SearchResultBookFragment()
    }

    private var _binding: FragmentSearchResultBinding? = null
    private val binding get() = _binding!!
    private val vm: SearchViewModel by activityViewModels()
    private lateinit var bookAdapter: BookAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupRecyclerView()

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

    private fun setupRecyclerView() {
        bookAdapter = BookAdapter { book ->
            val intent = Intent(requireContext(), BookActivity::class.java)
            intent.putExtra("book_detail_id", book.id)
            startActivity(intent)
        }

        val gridLayoutManager = GridLayoutManager(requireContext(), 3)

        binding.rv.apply {
            adapter = bookAdapter
            layoutManager = gridLayoutManager

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