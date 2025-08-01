package com.bonda.bonda.ui.home.books

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import coil3.load
import com.bonda.bonda.databinding.FragmentHomeBooksBinding
import com.bonda.bonda.databinding.ViewBookCategoryButtonBinding
import com.bonda.bonda.databinding.ViewBookHorizontalBinding
import com.bonda.bonda.databinding.ViewBookVerticalBinding
import com.bonda.bonda.databinding.ViewChipBookCategoryFilterBinding
import com.bonda.bonda.model.BookCategory
import com.bonda.bonda.model.BookTheme
import com.bonda.bonda.ui.book.BookActivity
import com.bonda.bonda.ui.search.SearchActivity

class BooksFragment : Fragment() {

    private var _binding: FragmentHomeBooksBinding? = null
    private val binding get() = _binding!!
    private val vm: BooksViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBooksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.searchButton.setOnClickListener {
            startActivity(Intent(requireContext(), SearchActivity::class.java))
        }

        /**
         * 방금 도착한 새로운 책 카테고리 binding
         */
        binding.popularChipGroup.removeAllViews()
        BookCategory.entries.forEachIndexed { index, category ->
            val itemBinding = ViewChipBookCategoryFilterBinding.inflate(
                layoutInflater,
                binding.popularChipGroup,
                false
            )
            itemBinding.root.text = category.label
            if (index == 0) itemBinding.root.isChecked = true
            itemBinding.root.setOnClickListener { vm.setSelectedNewArrivedBooksCategory(category.code) }

            binding.popularChipGroup.addView(itemBinding.root)
        }

        /**
         * 방금 도착한 새로운 책 binding
         */
        vm.recentArrivalBooks.observe(viewLifecycleOwner) { list ->
            binding.recentArrivalBooksContainer.removeAllViews()

            list.forEach { book ->
                val itemBinding = ViewBookVerticalBinding.inflate(
                    layoutInflater,
                    binding.recentArrivalBooksContainer,
                    false
                )

                itemBinding.coverImage.load(book.coverImage)
                itemBinding.title.text = book.title
                itemBinding.author.text = book.author
                itemBinding.category.root.text = book.category

                val params = itemBinding.root.layoutParams as GridLayout.LayoutParams
                params.width = 0
                params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                itemBinding.root.layoutParams = params

                // book 상세 페이지로 이동
                itemBinding.root.setOnClickListener {
                    val intent = Intent(requireContext(), BookActivity::class.java)
                    intent.putExtra("book_detail_id", book.id)
                    Log.d("DEBUG", "start_book_detail_activity_id : ${book.id}")
                    startActivity(intent)
                }

                binding.recentArrivalBooksContainer.addView(itemBinding.root)
            }
        }

        /**
         * 카테고리 버튼 binding
         */
        binding.categoriesGridContainer.removeAllViews()
        BookCategory.BUSINESS_CATEGORIES.forEach { category ->
            val itemBinding = ViewBookCategoryButtonBinding.inflate(
                layoutInflater,
                binding.categoriesGridContainer,
                false
            )

            itemBinding.apply {
                icText.text = category.label
                icImage.setImageResource(category.iconRes!!)
                root.setOnClickListener {
                    val intent = Intent(requireContext(), BooksCategoryActivity::class.java)
                    intent.putExtra("category_selected", category.code)
                    startActivity(intent)
                }
            }

            val params = itemBinding.root.layoutParams as GridLayout.LayoutParams
            params.width = 0
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
            itemBinding.root.layoutParams = params

            binding.categoriesGridContainer.addView(itemBinding.root)
        }

        /**
         * 요즘 가장 많이 사랑 받은 책 카테고리 binding
         */
        binding.lovedChipGroup.removeAllViews()
        BookTheme.entries.forEachIndexed { index, category ->
            val itemBinding = ViewChipBookCategoryFilterBinding.inflate(
                layoutInflater,
                binding.popularChipGroup,
                false
            )
            itemBinding.root.text = category.label
            if (index == 0) itemBinding.root.isChecked = true
            itemBinding.root.setOnClickListener { vm.setSelectedMostLovedBooksCategory(category.code) }

            binding.lovedChipGroup.addView(itemBinding.root)
        }

        /**
         * 요즘 가장 사랑 받은 책 binding
         */
        vm.mostLovedBooks.observe(viewLifecycleOwner) { list ->
            binding.mostLovedBooksContainer.removeAllViews()

            list.forEach { book ->
                val itemBinding = ViewBookHorizontalBinding.inflate(
                    layoutInflater,
                    binding.mostLovedBooksContainer,
                    false
                )

                itemBinding.coverImage.load(book.coverImage)
                itemBinding.title.text = book.title
                itemBinding.author.text = book.author
                itemBinding.category.root.text = book.category

                // book 상세 페이지로 이동
                itemBinding.root.setOnClickListener {
                    val intent = Intent(requireContext(), BookActivity::class.java)
                    intent.putExtra("book_detail_id", book.id)
                    Log.d("DEBUG", "start_book_detail_activity_id : ${book.id}")
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