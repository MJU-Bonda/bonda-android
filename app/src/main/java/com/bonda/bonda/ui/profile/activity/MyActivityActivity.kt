package com.bonda.bonda.ui.profile.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.bonda.bonda.R
import com.bonda.bonda.databinding.ActivityMyActivityBinding
import com.bonda.bonda.ui.modal.BadgeDetailViewModal

class MyActivityActivity : AppCompatActivity() {

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

        badgeViewBinds.forEachIndexed { index, badgeView ->
            badgeView.badgeImage.setImageResource(badgeImages[index])
            badgeView.badgeTitle.setText(badgeTitles[index])
            badgeView.root.setOnClickListener {
                BadgeDetailViewModal
                    .newInstance(
                        "발견의 마법사",
                        "조회한 도서 ( 01 / 10 )",
                        "10개의 도서를 조하여 '발견의 마법사'뱃지를 획득했어요",
                        badgeImages[index]
                    )
                    .show(supportFragmentManager, "TAG")
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
    private val badgeTitles by lazy {
        listOf(
            R.string.discover_badge1_title,
            R.string.discover_badge2_title,
            R.string.discover_badge3_title,
            R.string.discover_badge4_title,
            R.string.discover_badge5_title,
            R.string.discover_badge6_title,
            R.string.collect_badge1_title,
            R.string.collect_badge2_title,
            R.string.collect_badge3_title,
            R.string.collect_badge4_title,
            R.string.collect_badge5_title,
            R.string.collect_badge6_title
        )
    }
    private val badgeHints by lazy {
        listOf(
            R.string.discover_badge1_hint,
            R.string.discover_badge2_hint,
            R.string.discover_badge3_hint,
            R.string.discover_badge4_hint,
            R.string.discover_badge5_hint,
            R.string.discover_badge6_hint,
            R.string.collect_badge1_hint,
            R.string.collect_badge2_hint,
            R.string.collect_badge3_hint,
            R.string.collect_badge4_hint,
            R.string.collect_badge5_hint,
            R.string.collect_badge6_hint
        )
    }
    private val badgeDescriptions by lazy {
        listOf(
            R.string.discover_badge1_description,
            R.string.discover_badge2_description,
            R.string.discover_badge3_description,
            R.string.discover_badge4_description,
            R.string.discover_badge5_description,
            R.string.discover_badge6_description,
            R.string.collect_badge1_description,
            R.string.collect_badge2_description,
            R.string.collect_badge3_description,
            R.string.collect_badge4_description,
            R.string.collect_badge5_description,
            R.string.collect_badge6_description
        )
    }
}