package com.bonda.bonda.ui.modal

import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.bonda.bonda.databinding.ViewBadgeDetailModalBinding

class BadgeDetailViewModal : DialogFragment() {

    private var _binding: ViewBadgeDetailModalBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val ARG_TITLE = "arg_title"
        private const val ARG_SUBTITLE = "arg_subtitle"
        private const val ARG_BODY = "arg_body"
        private const val ARG_IMAGE = "arg_image"

        fun newInstance(
            title: String,
            subtitle: String,
            body: String,
            image: Int
        ): BadgeDetailViewModal {
            return BadgeDetailViewModal().apply {
                arguments = Bundle().apply {
                    putString(ARG_TITLE, title)
                    putString(ARG_SUBTITLE, subtitle)
                    putString(ARG_BODY, body)
                    putInt(ARG_IMAGE, image)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = ViewBadgeDetailModalBinding.inflate(inflater, container, false).also {
        _binding = it
    }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let { args ->
            binding.badgeTitle.text = args.getString(ARG_TITLE)
            binding.badgeSubtitle.text = args.getString(ARG_SUBTITLE)
            binding.badgeBody.text = args.getString(ARG_BODY)
            binding.badgeImage.setImageResource(
                args.getInt(ARG_IMAGE)
            )
        }

        binding.buttonClose.setOnClickListener {
            dismiss()
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.apply {
            setLayout(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
            setBackgroundDrawableResource(android.R.color.transparent)
            setDimAmount(0.5f)
            attributes = attributes.apply {
                windowAnimations = android.R.style.Animation_Dialog
                gravity = Gravity.CENTER
            }
        }
    }
}