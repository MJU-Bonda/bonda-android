package com.bonda.bonda.ui.home.library

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import com.bonda.bonda.R
import com.bonda.bonda.databinding.FragmentHomeLibraryScrollerBinding
import com.bonda.bonda.model.toSortOrder
import com.bonda.bonda.ui.article.ArticleActivity
import com.bonda.bonda.ui.book.BookActivity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LibraryScrollerFragment : Fragment() {

    private var _binding: FragmentHomeLibraryScrollerBinding? = null
    private val binding get() = _binding!!

    private val vm: LibraryViewModel by viewModels({ requireParentFragment() })
    private lateinit var booksAdapter: SavedBookPagingAdapter
    private lateinit var articlesAdapter: SavedArticlePagingAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeLibraryScrollerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        when (arguments?.getInt("position")) {
            /**
             * 저장한 도서 목록을 표시합니다.
             */
            0 -> {
                booksAdapter = SavedBookPagingAdapter { book ->
                    val intent = Intent(requireContext(), BookActivity::class.java)
                    intent.putExtra("book_detail_id", book.id)
                    startActivity(intent)
                }

                binding.rv.layoutManager = GridLayoutManager(requireContext(), 3)
                binding.rv.adapter = booksAdapter
                binding.rv.addItemDecoration(
                    HorizontalSpacingDecoration(
                        context = requireContext(),
                        horizontalDp = 32,
                        spanCount = 3
                    )
                )
                binding.rv.addItemDecoration(
                    ShelfDecoration(
                        context = requireContext(),
                        shelfResId = R.drawable.bg_bookshelf,
                        spanCount = 3,
                        offsetFromRowBottomDp = -2
                    )
                )

                vm.savedBookCount.observe(viewLifecycleOwner) {
                    if (it < 1000) binding.tvItemCount.text = it.toString()
                    else binding.tvItemCount.text = "999+"
                }

                viewLifecycleOwner.lifecycleScope.launch {
                    viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                        vm.savedBooksFlow.collectLatest { booksAdapter.submitData(it) }
                    }
                }

                viewLifecycleOwner.lifecycleScope.launch {
                    viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                        vm.bookSortOrder.collect {
                            binding.textSortIndicator.text = it.toSortOrder().label
                        }
                    }
                }

                binding.btSort.setOnClickListener { vm.toggleBookSortOrder() }
            }

            /**
             * 저장한 아티클 목록을 표시합니다.
             */
            1 -> {
                articlesAdapter = SavedArticlePagingAdapter { article ->
                    val intent = Intent(requireContext(), ArticleActivity::class.java)
                    intent.putExtra("article_detail_id", article.articleId)
                    startActivity(intent)
                }

                binding.rv.layoutManager = GridLayoutManager(requireContext(), 2)
                binding.rv.adapter = articlesAdapter

                vm.savedArticleCount.observe(viewLifecycleOwner) {
                    if (it < 1000) binding.tvItemCount.text = it.toString()
                    else binding.tvItemCount.text = "999+"
                }

                viewLifecycleOwner.lifecycleScope.launch {
                    viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                        vm.savedArticlesFlow.collectLatest { articlesAdapter.submitData(it) }
                    }
                }

                viewLifecycleOwner.lifecycleScope.launch {
                    viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                        vm.articleSortOrder.collect {
                            binding.textSortIndicator.text = it.toSortOrder().label
                        }
                    }
                }

                binding.btSort.setOnClickListener { vm.toggleArticleSortOrder() }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_TAB = "arg_tab"
        fun newInstance(position: Int): LibraryScrollerFragment {
            val fragment = LibraryScrollerFragment()
            val args = Bundle()
            args.putInt("position", position)
            fragment.arguments = args
            return fragment
        }
    }
}