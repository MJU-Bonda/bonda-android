package com.bonda.bonda.ui.profile

import android.app.Dialog
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowInsetsController
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.bonda.bonda.R
import com.bonda.bonda.databinding.ViewProfileImageSelectorDialogBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * output interface 정의
 */
fun interface OnImagePickedListener {
    fun onImagePicked(uri: Uri)
}

class ProfileImageSelectorView(
    private val listener: OnImagePickedListener
) : BottomSheetDialogFragment() {

    private var _binding: ViewProfileImageSelectorDialogBinding? = null
    private val binding get() = _binding!!
    private var _dialog: BottomSheetDialog? = null
    private val dialog get() = _dialog!!

    /**
     * 갤러리에서 사진 한장을 선택합니다
     */
    private val pickMedia =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            uri?.let { listener.onImagePicked(it) }
            dismiss()
        }

    /**
     * 카메라로 사진 한장을 촬영합니다
     */
    private var photoBitmap: Bitmap? = null
    private val takePictureLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicturePreview()) { bitmap: Bitmap? ->
            if (bitmap != null) {
                photoBitmap = bitmap
                val uri = saveBitmapToCacheAndGetUri(requireContext(), bitmap)
                uri?.let { listener.onImagePicked(it) }
                dismiss()
            }
        }

    /**
     * 카메라 권한 요청을 위한 ActivityResultLauncher
     */
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
            } else {
                Toast.makeText(requireContext(), "설정에서 카메라 권한을 허용해주세요", Toast.LENGTH_SHORT).show()
            }
        }

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
            checkCameraPermissionAndLaunch()
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

    /**
     * 카메라 권한을 확인하고 권한이 있으면 카메라 실행, 없으면 권한을 요청합니다
     */
    private fun checkCameraPermissionAndLaunch() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                launchCamera()
            }

            shouldShowRequestPermissionRationale(android.Manifest.permission.CAMERA) -> {
                requestPermissionLauncher.launch(android.Manifest.permission.CAMERA)
            }

            else -> {
                requestPermissionLauncher.launch(android.Manifest.permission.CAMERA)
            }
        }
    }

    /**
     * 카메라를 실행합니다
     */
    private fun launchCamera() {
        takePictureLauncher.launch(null)
    }

}

/**
 * Bitmap을 앱 내부 캐시 디렉터리에 파일로 저장하고,
 * 해당 파일에 대한 FileProvider Uri를 반환합니다.
 * @param context 컨텍스트
 * @param bitmap 저장할 비트맵
 * @return 생성된 파일의 Uri. 실패 시 null을 반환합니다.
 */
fun saveBitmapToCacheAndGetUri(context: Context, bitmap: Bitmap): Uri? {
    val cachePath = File(context.cacheDir, "images")
    try {
        cachePath.mkdirs()

        val file = File(cachePath, "image_${System.currentTimeMillis()}.jpg")
        val stream = FileOutputStream(file)

        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        stream.close()

        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )
    } catch (e: IOException) {
        e.printStackTrace()
    }
    return null
}
