package com.bonda.bonda.ui.profile.recent.books

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.bonda.bonda.databinding.FragmentRecentActivityBinding
import com.bonda.bonda.model.GridSpacingItemDecoration
import com.bonda.bonda.model.dpToPx
import com.bonda.bonda.ui.book.BookActivity

class BooksFragment : Fragment() {

    private var _binding: FragmentRecentActivityBinding? = null
    private val binding get() = _binding!!
    private val vm: BooksViewModel by viewModels()

    private val adapter by lazy {
        BookAdapter { book ->
            val intent = Intent(requireContext(), BookActivity::class.java).apply {
                putExtra("book_detail_id", book.id)
            }
            startActivity(intent)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecentActivityBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.container.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.container.adapter = adapter

        /**
         * item 사이 gap을 추가합니다
         */
        val horizontalSpacing = 10.dpToPx()
        val verticalSpacing = 20.dpToPx()
        binding.container.addItemDecoration(
            GridSpacingItemDecoration(2, horizontalSpacing, verticalSpacing)
        )

        vm.getBooks()

        vm.isLoading.observe(viewLifecycleOwner) { binding.progressIndicator.isVisible = it }
        vm.isError.observe(viewLifecycleOwner) { binding.errorNetwork.root.isVisible = it }
        vm.isEmpty.observe(viewLifecycleOwner) { binding.emptyBookListText.isVisible = it }
        vm.books.observe(viewLifecycleOwner) {
            adapter.submitList(it)
            binding.container.isVisible = it.isNotEmpty()
        }

        binding.errorNetwork.buttonRetry.setOnClickListener { vm.getBooks() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
