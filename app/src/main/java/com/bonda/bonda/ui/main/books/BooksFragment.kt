package com.bonda.bonda.ui.main.books

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModelProvider
import com.bonda.bonda.databinding.FragmentMainBookBinding
import com.bonda.bonda.databinding.ViewBookCategoryButtonBinding
import com.bonda.bonda.databinding.ViewBookHorizontalBinding
import com.bonda.bonda.databinding.ViewBookVerticalBinding
import com.bonda.bonda.databinding.ViewChipBookCategoryBinding
import com.bonda.bonda.ui.detail.book.BookActivity

class BooksFragment : Fragment() {

    private var _binding: FragmentMainBookBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBookBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val booksViewModel = ViewModelProvider(this)[BooksViewModel::class.java]

        // 카테고리 버튼 추가
        data class Category(
            val text: LiveData<String>,
            val icon: LiveData<Int>
        )

        val categories = listOf(
            Category(booksViewModel.categoryNovelButtonText,       booksViewModel.categoryNovelButtonIcon),
            Category(booksViewModel.categoryPoemButtonText,        booksViewModel.categoryPoemButtonIcon),
            Category(booksViewModel.categoryEssayButtonText,       booksViewModel.categoryEssayButtonIcon),
            Category(booksViewModel.categoryComicButtonText,       booksViewModel.categoryComicButtonIcon),
            Category(booksViewModel.categoryPhotoButtonText,       booksViewModel.categoryPhotoButtonIcon),
            Category(booksViewModel.categoryArtButtonText,         booksViewModel.categoryArtButtonIcon),
            Category(booksViewModel.categoryIllustrationButtonText,booksViewModel.categoryIllustrationButtonIcon),
            Category(booksViewModel.categoryMagazineButtonText,    booksViewModel.categoryMagazineButtonIcon)
        )

        categories.forEach { category ->
            val itemBinding = ViewBookCategoryButtonBinding.inflate(
                layoutInflater,
                binding.categoriesGridContainer,
                false
            )

            category.text.observe(viewLifecycleOwner) {
                itemBinding.icText.text = it
                itemBinding.icImage.contentDescription = it
            }
            category.icon.observe(viewLifecycleOwner) {
                itemBinding.icImage.setImageResource(it)
            }

            itemBinding.root.setOnClickListener {
                Toast.makeText(requireContext(), category.text.toString(), Toast.LENGTH_SHORT).show()
            }

            val params = itemBinding.root.layoutParams as GridLayout.LayoutParams
            params.width = 0
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
            itemBinding.root.layoutParams = params

            binding.categoriesGridContainer.addView(itemBinding.root)
        }

        // 방금 도착한 새로운 책 binding
        booksViewModel.recentArrivalBooks.observe(viewLifecycleOwner) { list ->
            binding.recentArrivalBooksContainer.removeAllViews()

            list.forEach { book ->
                val itemBinding = ViewBookVerticalBinding.inflate(
                    layoutInflater,
                    binding.recentArrivalBooksContainer,
                    false
                )

                itemBinding.bookImage.setImageResource(book.coverImage)
                itemBinding.bookTitle.text = book.title
                itemBinding.bookAuthor.text = book.author

                val chipBinding = ViewChipBookCategoryBinding.inflate(
                    layoutInflater,
                    itemBinding.bookCategoryChipGroup,
                    true
                )
                chipBinding.root.text = book.category

                val params = itemBinding.root.layoutParams as GridLayout.LayoutParams
                params.width = 0
                params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                itemBinding.root.layoutParams = params

                // book 상세 페이지로 이동
                itemBinding.root.setOnClickListener {
                    Log.d("DEBUG.START.BOOK.DETAIL.ACTIVITY", book.id.toString())
                    val intent = Intent(requireContext(), BookActivity::class.java)
                    startActivity(intent)
                }

                binding.recentArrivalBooksContainer.addView(itemBinding.root)
            }
        }

        // 요즘 가장 사랑 받은 책 binding
        booksViewModel.mostLovedBooks.observe(viewLifecycleOwner) { list ->
            binding.mostLovedBooksContainer.removeAllViews()

            list.forEach { book ->
                val itemBinding = ViewBookHorizontalBinding.inflate(
                    layoutInflater,
                    binding.mostLovedBooksContainer,
                    false
                )

                itemBinding.bookImage.setImageResource(book.coverImage)
                itemBinding.bookTitle.text = book.title
                itemBinding.bookAuthor.text = book.author

                val chipBinding = ViewChipBookCategoryBinding.inflate(
                    layoutInflater,
                    itemBinding.bookCategoryChipGroup,
                    true
                )
                chipBinding.root.text = book.category

                // book 상세 페이지로 이동
                itemBinding.root.setOnClickListener {
                    Log.d("DEBUG.START.BOOK.DETAIL.ACTIVITY", book.id.toString())
                    val intent = Intent(requireContext(), BookActivity::class.java)
                    startActivity(intent)
                }

                binding.mostLovedBooksContainer.addView(itemBinding.root)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}