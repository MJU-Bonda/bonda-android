package com.bonda.bonda.ui.home.profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.browser.customtabs.CustomTabsIntent
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import coil3.load
import com.bonda.bonda.databinding.FragmentHomeProfileBinding
import com.bonda.bonda.ui.profile.EditProfileActivity
import com.bonda.bonda.ui.profile.SettingsActivity
import com.bonda.bonda.ui.profile.activity.MyActivityActivity
import com.bonda.bonda.ui.profile.recent.RecentActivity
import androidx.core.net.toUri

class ProfileFragment : Fragment() {

    private var _binding: FragmentHomeProfileBinding? = null
    private val binding get() = _binding!!

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val vm = ViewModelProvider(this)[ProfileViewModel::class.java]

        vm.username.observe(viewLifecycleOwner) { binding.textUsername.text = it }
        vm.profileImage.observe(viewLifecycleOwner) {
            if (!it.isNullOrBlank()) {
                binding.profileImage.foreground = null
                binding.profileImage.load(it)
            }
        }
        vm.savedBookCount.observe(viewLifecycleOwner) { binding.buttonMyBooksCount.text = "${it}권" }
        vm.collectedBadgeCount.observe(viewLifecycleOwner) {
            binding.buttonMyBadgesCount.text = "${it}개"
        }

        binding.buttonProfile.setOnClickListener {
            startActivity(Intent(requireContext(), EditProfileActivity::class.java))
        }

        binding.settingsButton.setOnClickListener {
            startActivity(Intent(requireContext(), SettingsActivity::class.java))
        }

        binding.buttonRecentActivity.setOnClickListener {
            startActivity(Intent(requireContext(), RecentActivity::class.java))
        }

        binding.buttonActivity.setOnClickListener {
            startActivity(Intent(requireContext(), MyActivityActivity::class.java))
        }

        binding.buttonNotice.setOnClickListener {
            val url = "https://hulking-papaya-a80.notion.site/BONDA-2308b15a2d1d80e1a0a6d7e04c07f2b7"
            val builder = CustomTabsIntent.Builder()
            val customTabsIntent = builder.build()
            customTabsIntent.launchUrl(requireContext(), url.toUri())
        }

        binding.buttonPolicy.setOnClickListener {
            val url = "https://hulking-papaya-a80.notion.site/BONDA-1f88b15a2d1d8085a4d1c6fa47e1dfc7"
            val builder = CustomTabsIntent.Builder()
            val customTabsIntent = builder.build()
            customTabsIntent.launchUrl(requireContext(), url.toUri())
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