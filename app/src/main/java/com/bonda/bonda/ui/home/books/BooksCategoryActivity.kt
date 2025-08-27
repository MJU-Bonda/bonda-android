package com.bonda.bonda.ui.home.books

import android.content.Intent
import android.os.Bundle
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
import com.bonda.bonda.model.toSortOrder
import com.bonda.bonda.ui.book.BookActivity
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class BooksCategoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBooksCategoryBinding
    private lateinit var booksAdapter: BookPagingAdapter
    private val vm: BooksCategoryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityBooksCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        /**
         * activity 실행 시 카테고리 선택 값을 받아옵니다
         */
        val categorySelected = intent.getStringExtra("category_selected")
        vm.setSelectedCategory(categorySelected!!)

        /**
         * action bar 설정
         */
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        vm.selectedCategory.observe(this) { supportActionBar?.title = it.toBookCategory().label }

        /**
         * category chip을 화면에 표시합니다
         */
        vm.categories.observe(this) { categories ->
            binding.catgoryChipGroup.removeAllViews()

            categories.forEach { category ->

                val itemBinding = ViewChipBookCategoryFilterBinding.inflate(
                    layoutInflater,
                    binding.catgoryChipGroup,
                    false
                )
                itemBinding.root.text = category.toBookCategory().label
                if (categorySelected == category) {
                    itemBinding.root.isChecked = true
                }
                itemBinding.root.setOnClickListener { vm.setSelectedCategory(category) }

                binding.catgoryChipGroup.addView(itemBinding.root)
            }

        }

        /**
         * 총 도서 갯수를 표시합니다
         */
        vm.totalBookCount.observe(this) { binding.tvBookCount.text = it.toString() }

        /**
         * 도서 조회 결과를 rv 어댑터에 binding 합니다
         */
        booksAdapter = BookPagingAdapter { book ->
            val intent = Intent(this, BookActivity::class.java)
            intent.putExtra("book_detail_id", book.id)
            startActivity(intent)
        }

        binding.rv.layoutManager = GridLayoutManager(this, 3)
        binding.rv.adapter = booksAdapter

        lifecycleScope.launch { vm.booksFlow.collectLatest { booksAdapter.submitData(it) } }

        /**
         * 정렬 기준을 변경합니다
         */
        binding.btSort.setOnClickListener { vm.toggleSortOrder() }
        vm.orderBy.observe(this) {binding.textSortIndicator.text = it.toSortOrder().label}
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

}