package com.bonda.bonda.ui.home.profile

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bonda.bonda.databinding.FragmentHomeProfileBinding
import com.bonda.bonda.ui.ProfileSetupActivity
import com.bonda.bonda.ui.SignInActivity
import com.bonda.bonda.ui.onboarding.OnboardingActivity

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

        val profileViewModel = ViewModelProvider(this)[ProfileViewModel::class.java]

//        profileViewModel.text.observe(viewLifecycleOwner) {
//            binding.textProfile.text = it
//        }

        binding.buttonOnboarding.setOnClickListener {
            startActivity(Intent(requireContext(), OnboardingActivity::class.java))
        }
        binding.buttonBooks.setOnClickListener {
            startActivity(Intent(requireContext(), ProfileSetupActivity::class.java))
        }
        binding.buttonSignIn.setOnClickListener {
            startActivity(Intent(requireContext(), SignInActivity::class.java))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}