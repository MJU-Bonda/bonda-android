package com.bonda.bonda.ui.search

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bonda.bonda.databinding.FragmentSearchResultBookBinding
import com.bonda.bonda.ui.book.BookActivity

class SearchResultBookFragment: Fragment() {

    companion object {
        fun newInstance(): SearchResultBookFragment = SearchResultBookFragment()
    }

    private var _binding: FragmentSearchResultBookBinding? = null
    private val binding get() = _binding!!
    private val vm: SearchViewModel by activityViewModels()
    private lateinit var bookAdapter: BookAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchResultBookBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupRecyclerView()

        /**
         * 도서 검색 결과 바인딩
         */
        vm.booksSearchResult.observe(viewLifecycleOwner) { books ->
            binding.tvNoResult.visibility = if (books.isEmpty()) View.VISIBLE else View.GONE
            bookAdapter.submitList(books)
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

                    val lastVisibleItemPosition = gridLayoutManager.findLastCompletelyVisibleItemPosition()
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