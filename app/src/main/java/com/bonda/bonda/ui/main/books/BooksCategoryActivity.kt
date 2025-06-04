package com.bonda.bonda.ui.main.books

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.GridLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.ViewModelProvider
import com.bonda.bonda.databinding.ActivityBooksCategoryBinding
import com.bonda.bonda.databinding.ViewBookVerticalBinding
import com.bonda.bonda.ui.book.detail.BookActivity

class BooksCategoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBooksCategoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        var categorySelected = intent.getStringExtra("category_selected")
        enableEdgeToEdge()

        binding = ActivityBooksCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = categorySelected

        val booksCategoryViewModel= ViewModelProvider(this)[BooksCategoryViewModel::class.java]

        // 카테고리 chip binding
//        booksCategoryViewModel.categories.observe(this) { list ->
//            binding.categoryChipSelector.removeAllViews()
//
//            list.forEach { category ->
//                val chipBinding =  ViewSelectableChipBinding.inflate(
//                    layoutInflater,
//                    binding.categoryChipSelector,
//                    true
//                )
//                chipBinding.root.text = category
//            }
//        }

        // 도서 binding
        booksCategoryViewModel.books.observe(this) { list ->
            binding.booksGridContainer.removeAllViews()

            list.forEach { book ->
                val itemBinding = ViewBookVerticalBinding.inflate(
                    layoutInflater,
                    binding.booksGridContainer,
                    false
                )

                itemBinding.coverImage.setImageResource(book.coverImage)
                itemBinding.title.text = book.title
                itemBinding.author.text = book.author
                itemBinding.category.root.text = book.category

                val params = itemBinding.root.layoutParams as GridLayout.LayoutParams
                params.width = 0
                params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f)
                itemBinding.root.layoutParams = params

                // book 상세 페이지로 이동
                itemBinding.root.setOnClickListener {
                    val intent = Intent(this, BookActivity::class.java)
                    intent.putExtra("book_detail_id", book.id)
                    Log.d("DEBUG", "start_book_detail_activity_id : ${book.id}")
                    startActivity(intent)
                }

                binding.booksGridContainer.addView(itemBinding.root)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}