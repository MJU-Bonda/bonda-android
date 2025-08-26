package com.bonda.bonda.ui.search

import android.content.Context
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.bonda.bonda.databinding.ActivitySearchBinding
import com.bonda.bonda.databinding.ViewChipSearchHistoryBinding
import com.bonda.bonda.ui.profile.DialogView
import com.google.android.material.tabs.TabLayoutMediator

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private val searchResultTabTitles = listOf("전체", "도서", "아티클")
    val vm: SearchViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        /**
         * 검색 결과 tab binding
         */
        binding.searchResultViewpager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int = searchResultTabTitles.size
            override fun createFragment(position: Int): Fragment =
                SearchResultFragment.newInstance(searchResultTabTitles[position])
        }
        TabLayoutMediator(
            binding.searchResultTablayout,
            binding.searchResultViewpager
        ) { tab, pos ->
            tab.text = searchResultTabTitles[pos]
        }.attach()

        binding.buttonClose.setOnClickListener { finish() }
        binding.buttonToggleSaveHistory.setOnClickListener { vm.setIsHistoryActivated() }

        /**
         * 검색 기록 삭제
         */
        binding.buttonClearHistory.setOnClickListener {
            DialogView.newInstance(
                requestKey = "clear_history",
                message = "검색 기록을 모두 삭제하시겠습니까?",
                confirmText = "삭제",
                cancelText = "취소"
            ).show(supportFragmentManager, "clear_history_dialog")
        }
        supportFragmentManager.setFragmentResultListener(
            "clear_history", this
        ) { _, bundle ->
            if (bundle.getBoolean("isConfirmed", false)) {
                vm.removeAllSearchHistory()
            }
        }

        /**
         * 검색바 로직
         */
        binding.searchBar.setOnKeyListener { _, actionId, event ->
            val isSearchAction = actionId == EditorInfo.IME_ACTION_SEARCH ||
                        actionId == EditorInfo.IME_ACTION_DONE ||
                        (event?.keyCode == KeyEvent.KEYCODE_ENTER && event.action == KeyEvent.ACTION_DOWN)

            if (isSearchAction) {
                val query = binding.searchBar.text.toString().trim()
                if (query.isNotEmpty()) {
                    vm.clearSearch()
                    vm.search(query)
                    binding.searchResult.visibility = View.VISIBLE

                    // 키보드 숨기기
                    (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                        .hideSoftInputFromWindow(binding.searchBar.windowToken, 0)
                }
                true
            } else {
                false
            }
        }

        /**
         * 자동 저장 토글
         */
        vm.isHistoryActivated.observe(this) { activated ->
            if (activated) binding.buttonToggleSaveHistory.text = "자동저장 끄기"
            else binding.buttonToggleSaveHistory.text = "자동저장 켜기"
        }

        /**
         * 검색 기록 binding
         */
        vm.isSearchHistoryEmpty.observe(this) { isEmpty ->
            if (isEmpty) binding.textIsEmpty.visibility = View.VISIBLE
            else binding.textIsEmpty.visibility = View.GONE
        }
        vm.searchHistory.observe(this) { histories ->
            binding.searchHistoryChipGroup.removeAllViews()

            histories.forEach { history ->
                val chipBinding = ViewChipSearchHistoryBinding.inflate(
                    layoutInflater,
                    binding.searchHistoryChipGroup,
                    false
                )

                chipBinding.root.apply {
                    text = history
                    setOnClickListener {
                        binding.searchBar.setText(history)
                        binding.searchBar.setSelection(history.length)
                        binding.searchBar.requestFocus()

                        val imm =
                            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.showSoftInput(binding.searchBar, InputMethodManager.SHOW_IMPLICIT)
                    }
                    setOnCloseIconClickListener {
                        binding.searchHistoryChipGroup.removeView(it)
                        vm.removeSearchHistory(history)
                    }
                }

                binding.searchHistoryChipGroup.addView(chipBinding.root)
            }
        }

        /**
         * 추천 키워드 binding
         */
        vm.recommendedKeyword.observe(this) { keywords ->
            binding.todayKeywordsChipGroup.removeAllViews()

            keywords.forEach { keyword ->
                val chipBinding = ViewChipSearchHistoryBinding.inflate(
                    layoutInflater,
                    binding.todayKeywordsChipGroup,
                    false
                )

                chipBinding.root.apply {
                    text = keyword
                    setCloseIconVisible(false)
                    setOnClickListener {
                        binding.searchBar.setText(keyword)
                        binding.searchBar.setSelection(keyword.length)
                        binding.searchBar.requestFocus()

                        val imm =
                            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.showSoftInput(binding.searchBar, InputMethodManager.SHOW_IMPLICIT)
                    }
                }

                binding.todayKeywordsChipGroup.addView(chipBinding.root)
            }
        }
    }

    /**
     * 뒤로 가기 버튼 클릭 시 검색 결과 창 닫음
     */
    override fun onBackPressed() {
        if (binding.searchResult.isVisible) {
            binding.searchResult.visibility = View.GONE
            vm.clearSearch()
        } else {
            super.onBackPressed()
        }
    }
}