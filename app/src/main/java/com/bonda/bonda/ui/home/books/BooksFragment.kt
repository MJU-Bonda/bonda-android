package com.bonda.bonda.ui.home.books

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
import coil3.load
import com.bonda.bonda.R
import com.bonda.bonda.databinding.FragmentHomeBooksBinding
import com.bonda.bonda.databinding.ViewBookCategoryButtonBinding
import com.bonda.bonda.databinding.ViewBookHorizontalBinding
import com.bonda.bonda.databinding.ViewBookVerticalBinding
import com.bonda.bonda.ui.book.BookActivity
import com.bonda.bonda.ui.search.SearchActivity

class BooksFragment : Fragment() {

    private var _binding: FragmentHomeBooksBinding? = null

    private val binding get() = _binding!!

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

        val booksViewModel = ViewModelProvider(this)[BooksViewModel::class.java]

        // 카테고리 버튼 추가
        data class Category(
            val text: LiveData<String>,
            val icon: LiveData<Int>
        )

        binding.popularChipGroup.setOnCheckedChangeListener { group, checkedId ->
            when(checkedId){
                R.id.chipPopularCategory0 -> booksViewModel.getNewArrivedBooks("ALL")
                R.id.chipPopularCategory1 -> booksViewModel.getNewArrivedBooks("PLANT")
                R.id.chipPopularCategory2 -> booksViewModel.getNewArrivedBooks("COOKING")
                R.id.chipPopularCategory3 -> booksViewModel.getNewArrivedBooks("MUSIC")
                R.id.chipPopularCategory4 -> booksViewModel.getNewArrivedBooks("ART")
            }
        }

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

        // 요즘 가장 사랑 받은 책 binding
        booksViewModel.mostLovedBooks.observe(viewLifecycleOwner) { list ->
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