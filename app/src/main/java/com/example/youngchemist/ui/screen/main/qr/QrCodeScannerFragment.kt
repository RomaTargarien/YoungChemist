package com.example.youngchemist.ui.screen.main.qr

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import com.example.youngchemist.R
import com.example.youngchemist.databinding.FragmentQrCodeScannerBinding
import com.example.youngchemist.ui.screen.main.MainFragment
import com.example.youngchemist.ui.screen.main.qr.analyzer.CameraManager
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class QrCodeScannerFragment : Fragment() {

    private val viewModel: QrCodeFragmentViewModel by viewModels()
    private lateinit var binding: FragmentQrCodeScannerBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        FragmentQrCodeScannerBinding.inflate(inflater,container,false).also { binding = it }.root


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        val qrCodeRawValue = arguments?.getString(QR_CODE_RAW_VALUE)
        qrCodeRawValue?.let {
            val intent = Intent(Intent.ACTION_VIEW)
            val uri = Uri.parse("https://arvr.google.com/scene-viewer/1.0").buildUpon()
                .appendQueryParameter(
                    "file",
                    it
                )
                .appendQueryParameter("mode", "3d_only")
                .build()
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.setData(uri)
            intent.setPackage("com.google.ar.core")
            startActivity(intent)
        }
    }

    companion object {
        private const val QR_CODE_RAW_VALUE = "qr_raw_value"
        @JvmStatic
        fun newInstance(param1: String?) =
            QrCodeScannerFragment().apply {
                arguments = Bundle().apply {
                    putString(QR_CODE_RAW_VALUE, param1)
                }
            }
    }


}