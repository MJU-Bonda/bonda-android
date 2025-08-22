package com.bonda.bonda.ui.profile.activity

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.lifecycleScope
import com.bonda.bonda.R
import com.bonda.bonda.databinding.ActivityMyActivityBinding
import com.bonda.bonda.databinding.ViewGraphLegendBinding
import com.bonda.bonda.network.ApiClient
import com.bonda.bonda.ui.modal.BadgeDetailViewModal
import kotlinx.coroutines.launch

class MyActivityActivity : AppCompatActivity() {

    private val memberService = ApiClient.memberService

    private lateinit var binding: ActivityMyActivityBinding
    private val vm: MyActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityMyActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        /**
         * actionbar 로직
         */
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        /**
         * 탐색한 도서 및 수집한 도서 count binding
         */
        vm.viewedBookCount.observe(this) { binding.tvViewedBookCount.text = "${it}권" }
        vm.collectedBookCount.observe(this) { binding.tvSavedBookCount.text = "${it}권" }

        /**
         * 수집한 도서 갯수 별 막대그래프 binding
         */
        vm.collectedBookCategory.observe(this) { categories ->
            binding.graphContainer.removeAllViews()
            binding.graphContainer.weightSum =
                categories.sumOf { it.count }.toFloat()

            categories.forEachIndexed { index, category ->
                val params = LinearLayout.LayoutParams(
                    0,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    category.count.toFloat()
                )

                val item = ConstraintLayout(this).apply {
                    layoutParams = params
                    setBackgroundColor(
                        ContextCompat.getColor(
                            context,
                            when (index) {
                                0 -> R.color.surface_graph_tertiary
                                1 -> R.color.surface_graph_primary
                                2 -> R.color.surface_graph_secondary
                                else -> R.color.border_default_tertiary
                            }
                        )
                    )
                }

                binding.graphContainer.addView(item)
            }

            /**
             * 수집한 도서 갯수 별 범례 binding
             */
            binding.legendContainer.removeAllViews()
            categories.forEachIndexed { index, category ->
                val itemBinding =
                    ViewGraphLegendBinding.inflate(
                        layoutInflater,
                        binding.legendContainer,
                        false
                    )

                itemBinding.tvCategory.text = category.category
                itemBinding.tvCount.text = category.count.toString()

                val colorInt = ContextCompat.getColor(
                    this,
                    when (index) {
                        0 -> R.color.surface_graph_tertiary
                        1 -> R.color.surface_graph_primary
                        2 -> R.color.surface_graph_secondary
                        else -> R.color.border_default_tertiary
                    }
                )
                val tintList = ColorStateList.valueOf(colorInt)
                itemBinding.cvDot.backgroundTintList = tintList

                binding.legendContainer.addView(itemBinding.root)
            }
        }

        /**
         * 획득한 뱃지 list binding
         */
        vm.collectedBadgeCount.observe(this) { binding.tvBadgeCount.text = "${it}개" }
        vm.collectedBadgeList.observe(this) { badgeList ->
            if (badgeList.isEmpty()) return@observe

            badgeViewBinds.forEachIndexed { index, badgeView ->
                badgeView.badgeTitle.setText(badgeList[index].name)

                if (badgeList[index].isUnlocked) {
                    badgeView.badgeImage.setImageResource(badgeImages[index])
                } else {
                    badgeView.badgeImage.setImageResource(badgeDisabledImages[index])
                }

                badgeView.root.setOnClickListener {
                    lifecycleScope.launch {
                        val res = memberService.getBadgeDetail(badgeList[index].id).unwrapOrThrow()

                        if (badgeList[index].isUnlocked) {
                            BadgeDetailViewModal
                                .newInstance(
                                    res.name,
                                    res.acquiredDate!!,
                                    res.description,
                                    badgeDetailImages[index]
                                )
                                .show(supportFragmentManager, "TAG")
                        } else {
                            BadgeDetailViewModal
                                .newInstance(
                                    res.name,
                                    "${res.currentProgress} / ${res.goal}",
                                    res.description,
                                    badgeDetailImages[index]
                                )
                                .show(supportFragmentManager, "TAG")
                        }
                    }
                }
            }
        }

    }

    /**
     * 뱃지 이미지 프로퍼티
     */
    private val badgeViewBinds by lazy {
        listOf(
            binding.discoverBadge1,
            binding.discoverBadge2,
            binding.discoverBadge3,
            binding.discoverBadge4,
            binding.discoverBadge5,
            binding.discoverBadge6,
            binding.collectBadge1,
            binding.collectBadge2,
            binding.collectBadge3,
            binding.collectBadge4,
            binding.collectBadge5,
            binding.collectBadge6
        )
    }
    private val badgeImages by lazy {
        listOf(
            R.drawable.badge_discover_1,
            R.drawable.badge_discover_2,
            R.drawable.badge_discover_3,
            R.drawable.badge_discover_4,
            R.drawable.badge_discover_5,
            R.drawable.badge_discover_6,
            R.drawable.badge_collect_1,
            R.drawable.badge_collect_2,
            R.drawable.badge_collect_3,
            R.drawable.badge_collect_4,
            R.drawable.badge_collect_5,
            R.drawable.badge_collect_6
        )
    }
    private val badgeDisabledImages by lazy {
        listOf(
            R.drawable.badge_discover_1_disabled,
            R.drawable.badge_discover_2_disabled,
            R.drawable.badge_discover_3_disabled,
            R.drawable.badge_discover_4_disabled,
            R.drawable.badge_discover_5_disabled,
            R.drawable.badge_discover_6_disabled,
            R.drawable.badge_collect_1_disabled,
            R.drawable.badge_collect_2_disabled,
            R.drawable.badge_collect_3_disabled,
            R.drawable.badge_collect_4_disabled,
            R.drawable.badge_collect_5_disabled,
            R.drawable.badge_collect_6_disabled
        )
    }
    private val badgeDetailImages by lazy {
        listOf(
            R.drawable.badge_discover_1_detail,
            R.drawable.badge_discover_2_detail,
            R.drawable.badge_discover_3_detail,
            R.drawable.badge_discover_4_detail,
            R.drawable.badge_discover_5_detail,
            R.drawable.badge_discover_6_detail,
            R.drawable.badge_collect_1_detail,
            R.drawable.badge_collect_2_detail,
            R.drawable.badge_collect_3_detail,
            R.drawable.badge_collect_4_detail,
            R.drawable.badge_collect_5_detail,
            R.drawable.badge_collect_6_detail
        )
    }

}