package com.bonda.bonda.ui

import android.app.Dialog
import android.os.Bundle
import android.view.Window
import androidx.fragment.app.DialogFragment
import com.bonda.bonda.R
import com.bonda.bonda.databinding.ViewWithdrawalDialogBinding

class WithdrawalDialogView : DialogFragment() {

    private var _binding: ViewWithdrawalDialogBinding? = null
    private val binding get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = ViewWithdrawalDialogBinding.inflate(layoutInflater)

        val dialog = Dialog(requireContext(), R.style.Theme_Bonda_WithdrawalDialog).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(binding.root)
            setCanceledOnTouchOutside(true)
        }

        binding.buttonConfirm.setOnClickListener {
            /**
             * TODO 회원 탈퇴 구현하고 로그아웃 하고 모든 activity 종료하고 main activity 실행
             */
            dialog.dismiss()
        }
        binding.buttonCancel.setOnClickListener {
            dialog.dismiss()
        }

        return dialog
    }

    override fun onDetach() {
        super.onDetach()
        _binding = null
    }
}
