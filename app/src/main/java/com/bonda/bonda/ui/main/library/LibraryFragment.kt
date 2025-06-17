package com.bonda.bonda.ui.main.library

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bonda.bonda.databinding.FragmentMainLibraryBinding
import com.bonda.bonda.ui.onboarding.OnboardingActivity
import com.bonda.bonda.ui.signup.SignInActivity
import com.bonda.bonda.ui.signup.SignUpActivity
import com.bonda.bonda.ui.signup.SplashActivity

class LibraryFragment : Fragment() {

    private var _binding: FragmentMainLibraryBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainLibraryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val libraryViewModel =
            ViewModelProvider(this)[LibraryViewModel::class.java]

        libraryViewModel.text.observe(viewLifecycleOwner) {
            binding.textLibrary.text = it
        }

        binding.buttonOnboarding.setOnClickListener {
            val intent = Intent(requireContext(), OnboardingActivity::class.java)
            startActivity(intent)
        }

        binding.buttonBooks.setOnClickListener {
//            val intent = Intent(requireContext(), BooksCategoryActivity::class.java)
//            intent.putExtra("category_selected", "에세이")
//            startActivity(intent)

            Intent(requireContext(), SignUpActivity::class.java).also {
                startActivity(it)
            }
        }

        binding.buttonSignIn.setOnClickListener {
            Intent(requireContext(), SplashActivity::class.java).also {
                startActivity(it)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}