package com.bonda.bonda.ui.home.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.Fragment
import coil3.load
import com.bonda.bonda.databinding.FragmentHomeProfileBinding
import com.bonda.bonda.ui.profile.EditProfileActivity
import com.bonda.bonda.ui.profile.SettingsActivity
import com.bonda.bonda.ui.profile.activity.MyActivityActivity
import com.bonda.bonda.ui.profile.recent.RecentActivity
import androidx.core.net.toUri
import androidx.fragment.app.viewModels
import com.bonda.bonda.model.BONDA_NOTICE_URL
import com.bonda.bonda.model.BONDA_TERMS_OF_POLICY_URL

class ProfileFragment : Fragment() {

    private var _binding: FragmentHomeProfileBinding? = null
    private val binding get() = _binding!!
    private val vm: ProfileViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /**
         * 회원 정보 binding
         */
        vm.username.observe(viewLifecycleOwner) { binding.textUsername.text = it }
        vm.profileImage.observe(viewLifecycleOwner) {
            if (it.isNotBlank()) {
                binding.profileImage.foreground = null
                binding.profileImage.load(it)
            }
        }
        vm.savedBookCount.observe(viewLifecycleOwner) { binding.buttonMyBooksCount.text = "${it}권" }
        vm.collectedBadgeCount.observe(viewLifecycleOwner) {
            binding.buttonMyBadgesCount.text = "${it}개"
        }

        /**
         * 설정 버튼
         */
        binding.settingsButton.setOnClickListener {
            startActivity(Intent(requireContext(), SettingsActivity::class.java))
        }

        /**
         * 프로필 수정 버튼
         */
        binding.buttonProfile.setOnClickListener {
            startActivity(Intent(requireContext(), EditProfileActivity::class.java))
        }

        /**
         * 내 활동 버튼
         */
        binding.buttonActivity.setOnClickListener {
            startActivity(Intent(requireContext(), MyActivityActivity::class.java))
        }

        /**
         * 최근 본 컨텐츠 버튼
         */
        binding.buttonRecentActivity.setOnClickListener {
            startActivity(Intent(requireContext(), RecentActivity::class.java))
        }

        /**
         * 공지사항 버튼 클릭 시 웹 브라우저를 통해 공지사항 페이지를 표시합니다.
         */
        binding.buttonNotice.setOnClickListener {
            val builder = CustomTabsIntent.Builder()
            val customTabsIntent = builder.build()
            customTabsIntent.launchUrl(requireContext(), BONDA_NOTICE_URL.toUri())
        }

        /**
         * 이용약관 버튼 클릭 시 웹 브라우저를 통해 이용약관 페이지를 표시합니다.
         */
        binding.buttonPolicy.setOnClickListener {
            val builder = CustomTabsIntent.Builder()
            val customTabsIntent = builder.build()
            customTabsIntent.launchUrl(requireContext(), BONDA_TERMS_OF_POLICY_URL.toUri())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}