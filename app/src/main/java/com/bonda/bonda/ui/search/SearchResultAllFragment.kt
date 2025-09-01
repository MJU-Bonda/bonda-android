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
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.bonda.bonda.R
import com.bonda.bonda.databinding.FragmentSearchResultAllBinding
import com.bonda.bonda.model.GridSpacingItemDecoration
import com.bonda.bonda.model.dpToPx
import com.bonda.bonda.ui.article.ArticleActivity
import com.bonda.bonda.ui.book.BookActivity
import com.bonda.bonda.ui.components.BaseFragment

class SearchResultAllFragment : BaseFragment() {

    companion object {
        fun newInstance(): SearchResultAllFragment = SearchResultAllFragment()
    }

    private var _binding: FragmentSearchResultAllBinding? = null
    private val binding get() = _binding!!
    private val vm: SearchViewModel by activityViewModels()

    private lateinit var bookAdapter: BookAdapter
    private lateinit var articleAdapter: ArticleAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        /**
         * BaseFragment가 자신의 레이아웃을 생성하도록 함
         */
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /**
         * 컨텐츠 레이아웃을 inflate하고, BaseFragment의 content_frame에 추가합니다
         */
        _binding = FragmentSearchResultAllBinding.inflate(layoutInflater)
        setBaseContent(binding.root)

        setupRecyclerViews()

        /**
         * 로딩 상태를 관찰합니다
         */
        vm.isLoading.observe(viewLifecycleOwner) { isLoading ->
            showLoadingView(isLoading)
            binding.root.visibility = if (isLoading) View.INVISIBLE else View.VISIBLE
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
         * 도서 검색 결과 바인딩
         */
        vm.booksSearchResult.observe(viewLifecycleOwner) { books ->
            bookAdapter.submitList(books.take(6))

            /**
             * 도서 검색 결과가 없는 경우
             */
            val keyword = vm.searchKeyword
            if (books.isEmpty() && keyword.isNotEmpty()) {
                binding.tvBooksNoResult.visibility = View.VISIBLE

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

                binding.tvBooksNoResult.text = spannable

                /**
                 * 도서 검색 결과가 있는 경우
                 */
            } else {
                binding.tvBooksNoResult.visibility = View.GONE
            }

            binding.btBookAll.visibility = if (books.isEmpty()) View.GONE else View.VISIBLE
        }

        /**
         * 아티클 검색 결과 바인딩
         */
        vm.articlesSearchResult.observe(viewLifecycleOwner) { articles ->
            articleAdapter.submitList(articles.take(6))

            /**
             * 아티클 검색 결과가 없는 경우
             */
            val keyword = vm.searchKeyword
            if (articles.isEmpty() && keyword.isNotEmpty()) {
                binding.tvArticlesNoResult.visibility = View.VISIBLE

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

                binding.tvArticlesNoResult.text = spannable

                /**
                 * 아티클 검색 결과가 있는 경우
                 */
            } else {
                binding.tvArticlesNoResult.visibility = View.GONE
            }

            binding.btArticleAll.visibility = if (articles.isEmpty()) View.GONE else View.VISIBLE
        }

        /**
         * 검색 결과 개수 바인딩
         */
        vm.booksSearchResultCount.observe(viewLifecycleOwner) {
            binding.tvResultBookCount.text = if (it < 1000) it.toString() else "999+"
        }
        vm.articlesSearchResultCount.observe(viewLifecycleOwner) {
            binding.tvResultArticleCount.text = if (it < 1000) it.toString() else "999+"
        }

        /**
         * 검색 결과 전체 보기 버튼
         */
        val pager = requireActivity().findViewById<ViewPager2>(R.id.search_result_viewpager)
        binding.btBookAll.setOnClickListener { pager.currentItem = 1 }
        binding.btArticleAll.setOnClickListener { pager.currentItem = 2 }
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
    private fun setupRecyclerViews() {
        /**
         * BookAdapter 초기화 (클릭 리스너 포함)
         */
        bookAdapter = BookAdapter { book ->
            val intent = Intent(requireContext(), BookActivity::class.java)
            intent.putExtra("book_detail_id", book.id)
            startActivity(intent)
        }

        /**
         * ArticleAdapter 초기화 (클릭 리스너 포함)
         */
        articleAdapter = ArticleAdapter { article ->
            val intent = Intent(requireContext(), ArticleActivity::class.java)
            intent.putExtra("article_detail_id", article.id)
            startActivity(intent)
        }

        /**
         * 도서 RecyclerView 설정
         */
        binding.gridBooks.apply {
            adapter = bookAdapter
            layoutManager = GridLayoutManager(requireContext(), 3)
            addItemDecoration(
                GridSpacingItemDecoration(3, 12.dpToPx(), 24.dpToPx())
            )
        }

        /**
         * 아티클 RecyclerView 설정
         */
        binding.gridArticles.apply {
            adapter = articleAdapter
            layoutManager = GridLayoutManager(requireContext(), 2)
            addItemDecoration(
                GridSpacingItemDecoration(2, 10.dpToPx(), 16.dpToPx())
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}