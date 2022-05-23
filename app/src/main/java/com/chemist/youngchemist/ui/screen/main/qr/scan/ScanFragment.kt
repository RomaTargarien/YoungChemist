package com.chemist.youngchemist.ui.screen.main.qr.scan

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.chemist.youngchemist.databinding.FragmentScanBinding
import com.chemist.youngchemist.ui.screen.main.qr.analyzer.CameraManager
import com.chemist.youngchemist.ui.util.slideUp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ScanFragment : Fragment() {

    private lateinit var binding: FragmentScanBinding
    private lateinit var cameraManager: CameraManager
    private val viewModel: ScanFragmentViewModel by viewModels()
    private var lastSelectedItemPosition: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            lastSelectedItemPosition = it.getInt(LAST_ITEM)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        FragmentScanBinding.inflate(inflater,container,false).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        createCameraManager()
        binding.tvQrCodeHelper.slideUp(requireContext(),300,500)
        if (allPermissionsGranted()) {
            cameraManager.startCamera()
        } else {
            requestPermissions(REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        requireActivity().onBackPressedDispatcher.addCallback {
            viewModel.exit(lastSelectedItemPosition)
        }
        cameraManager.qrCodeImageAnalyzer.qrCodeId.observe(viewLifecycleOwner, {
            viewModel.navigateToQrCodeScreen(it,lastSelectedItemPosition)
        })
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                cameraManager.startCamera()
            } else {
                Toast.makeText(this.requireContext(), "Permissions were not granted", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(this.requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }

    private fun createCameraManager() {
        cameraManager = CameraManager(this.requireContext(), binding.surfaceView, this)
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(android.Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
        private const val LAST_ITEM = "last_item"

        @JvmStatic
        fun newInstance(lastSelectedItemPosition: Int) =
            ScanFragment().apply {
                arguments = Bundle().apply {
                    putInt(LAST_ITEM,lastSelectedItemPosition)
                }
            }
    }
}