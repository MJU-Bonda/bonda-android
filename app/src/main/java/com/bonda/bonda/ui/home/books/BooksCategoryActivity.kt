package com.bonda.bonda.ui.home.books

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.bonda.bonda.databinding.ActivityBooksCategoryBinding
import com.bonda.bonda.databinding.ViewChipBookCategoryFilterBinding
import com.bonda.bonda.model.toBookCategory
import com.bonda.bonda.ui.book.BookActivity
import com.bonda.bonda.util.TAG
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class BooksCategoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBooksCategoryBinding
    private lateinit var booksAdapter: BookPagingAdapter
    private val vm: BooksCategoryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val categorySelected = intent.getStringExtra("category_selected")
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

        booksAdapter = BookPagingAdapter {book ->
            val intent = Intent(this, BookActivity::class.java)
            intent.putExtra("book_detail_id", book.id)
            startActivity(intent)
        }

        binding.rv.layoutManager = GridLayoutManager(this, 3)
        binding.rv.adapter = booksAdapter

        lifecycleScope.launch { vm.booksFlow.collectLatest { booksAdapter.submitData(it) } }

        vm.totalBookCount.observe(this){ binding.tvBookCount.text = it.toString() }
        vm.categories.observe(this) { categories ->
            binding.catgoryChipGroup.removeAllViews()

            categories.map { category ->

                val itemBinding = ViewChipBookCategoryFilterBinding.inflate(
                    layoutInflater,
                    binding.catgoryChipGroup,
                    false
                )
                itemBinding.root.text = category.toBookCategory().label
                if(categorySelected == category) { itemBinding.root.isChecked = true }
                itemBinding.root.setOnClickListener {
                    vm.selectCategory(category)

                }

                binding.catgoryChipGroup.addView(itemBinding.root)
            }

        }




        /**
         *         viewLifecycleOwner.lifecycleScope.launch {
         *             vm.savedBooksFlow.collectLatest { booksAdapter.submitData(it) }
         *         }
         *
         *         vm.savedBookCount.observe(viewLifecycleOwner) {
         *             binding.tvItemCount.text = it.toString()
         *         }
         *
         *         binding.btSort.setOnClickListener {
         *             // TODO 정렬 기능 구현해야함
         *         }
         */

    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

}