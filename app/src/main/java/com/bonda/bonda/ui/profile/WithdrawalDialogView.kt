package com.bonda.bonda.ui.profile

import android.app.Dialog
import android.os.Bundle
import android.view.Window
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import com.bonda.bonda.R
import com.bonda.bonda.databinding.ViewDialogBinding

class DialogView : DialogFragment() {

    private var _binding: ViewDialogBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val ARG_REQUEST_KEY = "ARG_REQUEST_KEY"
        private const val ARG_MESSAGE = "ARG_MESSAGE"
        private const val ARG_CONFIRM = "ARG_CONFIRM"
        private const val ARG_CANCEL = "ARG_CANCEL"

        fun newInstance(
            requestKey: String,
            message: String,
            confirmText: String = "확인",
            cancelText: String = "취소"
        ): DialogView {
            return DialogView().apply {
                arguments = Bundle().apply {
                    putString(ARG_REQUEST_KEY, requestKey)
                    putString(ARG_MESSAGE, message)
                    putString(ARG_CONFIRM, confirmText)
                    putString(ARG_CANCEL, cancelText)
                }
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = ViewDialogBinding.inflate(layoutInflater)

        val reqKey     = requireArguments().getString(ARG_REQUEST_KEY)!!
        val message = requireArguments().getString(ARG_MESSAGE)
        val confirmText = requireArguments().getString(ARG_CONFIRM)
        val cancelText = requireArguments().getString(ARG_CANCEL)

        binding.text.text = message
        binding.buttonConfirm.text = confirmText
        binding.buttonCancel.text = cancelText

        val dialog = Dialog(requireContext(), R.style.Theme_Bonda_Dialog).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(binding.root)
            setCanceledOnTouchOutside(true)
        }

        binding.buttonConfirm.setOnClickListener {
            parentFragmentManager.setFragmentResult(
                reqKey,
                bundleOf("isConfirmed" to true)
            )
            dialog.dismiss()
        }
        binding.buttonCancel.setOnClickListener {
            parentFragmentManager.setFragmentResult(
                reqKey,
                bundleOf("isConfirmed" to false)
            )
            dialog.dismiss()
        }

        return dialog
    }

    override fun onDetach() {
        super.onDetach()
        _binding = null
    }
}
