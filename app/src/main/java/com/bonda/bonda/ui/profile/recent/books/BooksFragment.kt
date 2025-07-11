package com.bonda.bonda.ui.profile.recent.books

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.bonda.bonda.databinding.FragmentRecentBooksBinding
import com.bonda.bonda.ui.book.BookActivity

class BooksFragment : Fragment() {

    private var _binding: FragmentRecentBooksBinding? = null
    private val binding get() = _binding!!

    val books = listOf(
        Book(1, "https://…/book1.jpg", "에세이", "골라골라 나 같은 집", "스펙타클 편집부"),
        Book(2, "https://…/book2.jpg", "소설", "어쩌구 저쩌구", "작가 이름"),
        Book(3, "https://…/book2.jpg", "소설", "어쩌구 저쩌구", "작가 이름"),
        Book(4, "https://…/book2.jpg", "소설", "어쩌구 저쩌구", "작가 이름"),
    )

    private val adapter by lazy {
        BookAdapter(books) { book ->

            val intent = Intent(requireContext(), BookActivity::class.java).apply {
                putExtra("BOOK_ID", book.id)
            }

            startActivity(intent)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecentBooksBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.root.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.root.adapter = adapter

        val vm = ViewModelProvider(this) [BooksViewModel::class.java]
        vm.getBooks()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
