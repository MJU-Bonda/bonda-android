package com.bonda.bonda.ui.home.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bonda.bonda.databinding.FragmentHomeProfileBinding
import com.bonda.bonda.ui.EditProfileActivity
import com.bonda.bonda.ui.SettingsActivity
import com.bonda.bonda.ui.history.RecentActivityActivity

class ProfileFragment : Fragment() {

    private var _binding: FragmentHomeProfileBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.buttonProfile.setOnClickListener {
            startActivity(Intent(requireContext(), EditProfileActivity::class.java))
        }

        binding.settingsButton.setOnClickListener{
            startActivity(Intent(requireContext(), SettingsActivity::class.java))
        }

        binding.buttonRecentActivity.setOnClickListener {
            startActivity(Intent(requireContext(), RecentActivityActivity::class.java))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}