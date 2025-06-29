package com.bonda.bonda.ui.profile

import android.app.Dialog
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsetsController
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import com.bonda.bonda.R
import com.bonda.bonda.databinding.ViewProfileImageSelectorDialogBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

/**
 * output interface 정의
 */
fun interface OnImagePickedListener {
    fun onImagePicked(uri: Uri)
}

class ProfileImageSelectorView(
    private val listener: OnImagePickedListener
) : BottomSheetDialogFragment() {

    // Registers a photo picker activity launcher in single-select mode.
    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        // Callback is invoked after the user selects a media item or closes the
        // photo picker.
        if (uri != null) {
            Log.d("PhotoPicker", "Selected URI: $uri")
        } else {
            Log.d("PhotoPicker", "No media selected")
        }

        uri?.let { listener.onImagePicked(it) }

        dismiss()
    }

    private var _binding: ViewProfileImageSelectorDialogBinding? = null
    private val binding get() = _binding!!

    private var _dialog: BottomSheetDialog? = null
    private val dialog get() = _dialog!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _dialog = BottomSheetDialog(requireContext(), R.style.Theme_Bonda_BottomSheetDialog)
        return dialog
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = ViewProfileImageSelectorDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.buttonGallery.setOnClickListener {
            pickMedia.launch(
                PickVisualMediaRequest(
                    ActivityResultContracts.PickVisualMedia.ImageOnly
                )
            )
        }

        binding.buttonCamera.setOnClickListener {
            dismiss()
        }

        dialog.window?.insetsController?.setSystemBarsAppearance(
            WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS,
            WindowInsetsController.APPEARANCE_LIGHT_NAVIGATION_BARS
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        _dialog = null
    }
}
