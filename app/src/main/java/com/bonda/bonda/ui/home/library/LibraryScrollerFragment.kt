package com.bonda.bonda.ui.home.library

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.bonda.bonda.R
import com.bonda.bonda.databinding.FragmentHomeLibraryScrollerBinding
import com.bonda.bonda.ui.book.BookActivity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class LibraryScrollerFragment : Fragment() {

    private var _binding: FragmentHomeLibraryScrollerBinding? = null
    private val binding get() = _binding!!

    private val vm: LibraryViewModel by viewModels({ requireParentFragment() })
    private lateinit var adapter: SavedBookPagingAdapter

    private fun setupBooksRecycler() {
        adapter = SavedBookPagingAdapter { book ->
            val intent = Intent(requireContext(), BookActivity::class.java)
            intent.putExtra("book_detail_id", book.id)
            startActivity(intent)
        }

        binding.rv.layoutManager = GridLayoutManager(requireContext(), 3)

        binding.rv.addItemDecoration(
            ShelfDecoration(
                context = requireContext(),
                shelfResId = R.drawable.bg_bookshelf,
                spanCount = 3,
                offsetFromRowBottomDp = 6
            )
        )

        binding.rv.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            vm.savedBooksFlow.collectLatest { adapter.submitData(it) }
        }
    }

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
            0 -> { // 저장한 도서 표시
                setupBooksRecycler()
            }

            1 -> { // 저장한 아티클 표시

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