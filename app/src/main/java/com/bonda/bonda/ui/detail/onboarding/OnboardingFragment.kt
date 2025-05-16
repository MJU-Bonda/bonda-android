package com.bonda.bonda.ui.detail.onboarding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.StringRes
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import com.bonda.bonda.R
import com.bonda.bonda.databinding.FragmentOnboardingBinding

class OnboardingFragment: Fragment(R.layout.fragment_onboarding) {

    private var _binding: FragmentOnboardingBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val ARG_HEADING1 = "arg_heading1"
        private const val ARG_HEADING2 = "arg_heading2"
        private const val ARG_IMAGE = "arg_image"
        fun newInstance(
            @StringRes heading1: Int,
            @StringRes heading2: Int,
//            image: Int
        ) = OnboardingFragment().apply {
            arguments = bundleOf(
                ARG_HEADING1 to heading1,
                ARG_HEADING2 to heading2,
//                ARG_IMAGE to image
            )
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentOnboardingBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val heading1 = requireArguments().getInt(ARG_HEADING1)
        val heading2 = requireArguments().getInt(ARG_HEADING2)
        // TODO: 이미지 리소스 로드

        binding.heading1.setText(heading1)
        binding.heading2.setText(heading2)
        // TODO: 이미지 리소스 바인딩
    }
}