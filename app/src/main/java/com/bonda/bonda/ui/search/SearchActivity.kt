package com.bonda.bonda.ui.search

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.ViewModelProvider
import com.bonda.bonda.databinding.ActivitySearchBinding
import com.bonda.bonda.databinding.ViewChipSearchHistoryBinding
import com.bonda.bonda.databinding.ViewChipSearchRecommendBinding
import com.bonda.bonda.util.TAG

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding

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

        val viewModel = ViewModelProvider(this)[SearchViewModel::class.java]

        binding.buttonClose.setOnClickListener {
            finish()
        }

        binding.buttonClearHistory.setOnClickListener {
            viewModel.removeAllSearchHistory()
            binding.searchHistoryChipGroup.removeAllViews()
        }

        binding.buttonToggleSaveHistory.setOnClickListener {
            viewModel.setIsHistoryActivated()
        }

        viewModel.isHistoryActivated.observe(this) { activated ->
            if (activated)
                binding.buttonToggleSaveHistory.text = "자동저장 끄기"
            else
                binding.buttonToggleSaveHistory.text = "자동저장 켜기"
        }

        viewModel.isEmpty.observe(this) { isEmpty ->
            if (isEmpty)
                binding.textIsEmpty.visibility = View.VISIBLE
            else
                binding.textIsEmpty.visibility = View.GONE
        }

        viewModel.searchHistory.observe(this) { histories ->
            binding.searchHistoryChipGroup.removeAllViews()

            histories.forEach { history ->
                val chipBinding = ViewChipSearchHistoryBinding.inflate(
                    layoutInflater,
                    binding.searchHistoryChipGroup,
                    false
                ).apply {
                    root.text = history
                    root.setOnCloseIconClickListener {
                        Log.d(TAG, "칩 삭제됨")
                        binding.searchHistoryChipGroup.removeView(root)
                        viewModel.removeSearchHistory(history)
                    }
                    root.setOnClickListener {
                        // TODO 클릭 시 검색바에 텍스트가 적용되도록
                    }
                }

                binding.searchHistoryChipGroup.addView(chipBinding.root)
            }
        }

        viewModel.recommendedKeyword.observe(this) { keywords ->
            binding.todayKeywordsChipGroup.removeAllViews()

            keywords.forEach { keyword ->
                val chipBinding = ViewChipSearchRecommendBinding.inflate(
                    layoutInflater,
                    binding.todayKeywordsChipGroup,
                    false
                ).apply {
                    root.text = keyword
                    root.setOnClickListener {
                        // TODO 클릭 시 검색바에 텍스트가 적용되도록
                    }
                }

                binding.todayKeywordsChipGroup.addView(chipBinding.root)
            }
        }
    }
}