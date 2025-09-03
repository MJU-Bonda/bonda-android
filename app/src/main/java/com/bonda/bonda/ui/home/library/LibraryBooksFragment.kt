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
import com.bonda.bonda.databinding.FragmentHomeLibraryBooksBinding
import com.bonda.bonda.model.toSortOrder
import com.bonda.bonda.ui.book.BookActivity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LibraryBooksFragment : Fragment() {

    private var _binding: FragmentHomeLibraryBooksBinding? = null // 변경된 부분
    private val binding get() = _binding!!

    private val vm: LibraryViewModel by viewModels({ requireParentFragment() })
    private lateinit var booksAdapter: SavedBookPagingAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeLibraryBooksBinding.inflate(inflater, container, false) // 변경된 부분
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 도서 목록 표시 로직만 남김
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

        /**
         * RecyclerView가 실제로 화면에 그려지고 사이즈가 결정된 후에 ItemDecoration을 다시 그리도록 요청합니다.
         */
        binding.rv.post {
            binding.rv.invalidateItemDecorations()
        }

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

        /**
         * 정렬순서 변경 버튼
         */
        binding.btSort.setOnClickListener { vm.toggleBookSortOrder() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}