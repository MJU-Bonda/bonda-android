package com.bonda.bonda.ui.profile.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bonda.bonda.R
import com.bonda.bonda.databinding.ActivityMyActivityBinding
import com.bonda.bonda.network.ApiClient
import com.bonda.bonda.ui.modal.BadgeDetailViewModal
import kotlinx.coroutines.launch

class MyActivityActivity : AppCompatActivity() {

    private val memberService = ApiClient.memberService

    lateinit var binding: ActivityMyActivityBinding

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

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }

        val vm = ViewModelProvider(this)[MyActivityViewModel::class.java]

        vm.viewedBookCount.observe(this) { binding.tvViewedBookCount.text = "지금까지 총 ${it}권을 탐색했고," }
        vm.collectedBookCount.observe(this) { binding.tvSavedBookCount.text = "그중 ${it}권을 수집했어요." }
        vm.collectedBadgeCount.observe(this) { binding.tvBadgeCount.text = "총 ${it}개의 뱃지를 획득했어요." }

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