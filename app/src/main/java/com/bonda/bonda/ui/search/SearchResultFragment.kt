package com.bonda.bonda.ui.search

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import coil3.load
import com.bonda.bonda.R
import com.bonda.bonda.databinding.FragmentSearchResultAllBinding
import com.bonda.bonda.databinding.ViewArticleBinding
import com.bonda.bonda.databinding.ViewBookVerticalBinding
import com.bonda.bonda.databinding.ViewRecentArticleBinding
import com.bonda.bonda.ui.article.ArticleActivity
import com.bonda.bonda.ui.book.BookActivity
import com.bonda.bonda.util.TAG

class SearchResultFragment : Fragment() {
    companion object {
        private const val ARG_CATEGORY = "arg_category"

        fun newInstance(category: String): SearchResultFragment =
            SearchResultFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_CATEGORY, category)
                }
            }
    }

    private val category: String by lazy {
        requireArguments().getString(ARG_CATEGORY)!!
    }

    private var _binding: FragmentSearchResultAllBinding? = null
    private val binding get() = _binding!!
    private val vm: SearchViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchResultAllBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        /**
         * 도서 검색 결과가 없는 경우
         */
        vm.booksSearchResult.observe(viewLifecycleOwner) {
            if (it.isEmpty()) binding.tvBooksNoResult.visibility = View.VISIBLE
            else binding.tvBooksNoResult.visibility = View.GONE
        }

        /**
         * 아티클 검색 결과가 없는 경우
         */
        vm.articlesSearchResult.observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                binding.tvArticlesNoResult.visibility = View.VISIBLE
                binding.btArticleAll.visibility = View.GONE
            } else {
                binding.tvArticlesNoResult.visibility = View.GONE
                binding.btArticleAll.visibility = View.VISIBLE
            }
        }

        /**
         * 도서 검색 결과 binding
         */
        vm.booksSearchResult.observe(viewLifecycleOwner) { books ->
            binding.gridBooks.removeAllViews()

            books.map { book ->
                val itemBinding = ViewBookVerticalBinding.inflate(
                    layoutInflater,
                    binding.gridBooks,
                    false
                )

                itemBinding.coverImage.load(book.imageUrl)
                itemBinding.title.text = book.title
                itemBinding.author.text = book.subtitle
                itemBinding.category.root.text = book.category

                val params = itemBinding.root.layoutParams as GridLayout.LayoutParams
                params.width = 0
                params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                itemBinding.root.layoutParams = params

                itemBinding.root.setOnClickListener {
                    val intent = Intent(requireContext(), BookActivity::class.java)
                    intent.putExtra("book_detail_id", book.id)
                    Log.d(TAG, "start_book_detail_activity_id : ${book.id}")
                    startActivity(intent)
                }

                binding.gridBooks.addView(itemBinding.root)
            }
        }

        /**
         * 아티클 검색 결과 binding
         */
        vm.articlesSearchResult.observe(viewLifecycleOwner) { articles ->
            binding.gridArticles.removeAllViews()

            articles.map { article ->
                val itemBinding = ViewRecentArticleBinding.inflate(
                    layoutInflater,
                    binding.gridArticles,
                    false
                )

                itemBinding.title.text = article.title
                itemBinding.category.root.text = article.category
                itemBinding.image.load(article.imageUrl)

                val params = itemBinding.root.layoutParams as GridLayout.LayoutParams
                params.width = 0
                params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                itemBinding.root.layoutParams = params

                itemBinding.root.setOnClickListener {
                    val intent = Intent(requireContext(), ArticleActivity::class.java)
                    intent.putExtra("article_detail_id", article.id)
                    Log.d(TAG, "start_article_detail_activity_id : ${article.id}")
                    startActivity(intent)
                }

                binding.gridArticles.addView(itemBinding.root)
            }
        }

        /**
         * 검색 결과 갯수 binding
         */
        vm.booksSearchResultCount.observe(viewLifecycleOwner) { binding.tvResultBookCount.text = it.toString()}
        vm.articlesSearchResultCount.observe(viewLifecycleOwner) { binding.tvResultArticleCount.text = it.toString()}

        /**
         * 검색 결과 전체 보기 버튼
         */
        val pager = requireActivity().findViewById<ViewPager2>(R.id.search_result_viewpager)
        binding.btBookAll.setOnClickListener { pager.currentItem = 1 }
        binding.btArticleAll.setOnClickListener { pager.currentItem = 2 }

        /**
         * 받은 카테고리 값으로 화면 구성 변경
         */
        when (category) {
            "전체" -> {
                /**
                 * 도서 검색 결과가 없는 경우
                 */
                vm.booksSearchResult.observe(viewLifecycleOwner) {
                    if (it.isEmpty()) binding.btBookAll.visibility = View.GONE
                    else binding.btBookAll.visibility = View.VISIBLE
                }

                /**
                 * 아티클 검색 결과가 없는 경우
                 */
                vm.articlesSearchResult.observe(viewLifecycleOwner) {
                    if (it.isEmpty()) {
                        binding.tvArticlesNoResult.visibility = View.VISIBLE
                        binding.btArticleAll.visibility = View.GONE
                    } else {
                        binding.tvArticlesNoResult.visibility = View.GONE
                        binding.btArticleAll.visibility = View.VISIBLE
                    }
                }
            }

            "도서" -> {
                binding.articlesContainer.visibility = View.GONE
                binding.btBookAll.visibility = View.GONE
            }

            "아티클" -> {
                binding.booksContainer.visibility = View.GONE
                binding.btArticleAll.visibility = View.GONE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}