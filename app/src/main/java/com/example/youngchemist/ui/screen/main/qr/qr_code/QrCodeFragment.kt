package com.example.youngchemist.ui.screen.main.qr.qr_code

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.youngchemist.databinding.FragmentQrCodeScannerBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class QrCodeFragment : Fragment() {

    private val viewModel: QrCodeFragmentViewModel by viewModels()
    private lateinit var binding: FragmentQrCodeScannerBinding
    private var qrCodeRawValue: String? = null
    private var lastSelectedItemPosition: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            qrCodeRawValue = it.getString(QR_CODE_RAW_VALUE)
            lastSelectedItemPosition = it.getInt(LAST_ITEM)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        FragmentQrCodeScannerBinding.inflate(inflater, container, false).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        requireActivity().onBackPressedDispatcher.addCallback {
            viewModel.exit(lastSelectedItemPosition)
        }
        binding.ivExit.setOnClickListener {
            viewModel.exit(lastSelectedItemPosition)
        }
        qrCodeRawValue?.let {
            if (it.isNotEmpty()) {
                startActivity(createIntent(it))
            }
        }

        lifecycleScope.launch(Dispatchers.Default) {
            delay(5000)
            lifecycleScope.launch(Dispatchers.Main) {
                binding.appCompatButton3.isVisible = true
                binding.appCompatButton2.isVisible = true
            }
        }
    }

    private fun createIntent(qrCodeRawValue: String): Intent {
        val intent = Intent(Intent.ACTION_VIEW)
        val uri = Uri.parse(AR_URI).buildUpon()
            .appendQueryParameter(FILE, qrCodeRawValue)
            .appendQueryParameter(MODE, MODE_TYPE)
            .build()
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.data = uri
        intent.setPackage(PACKAGE_NAME)
        return intent
    }

    companion object {
        private const val QR_CODE_RAW_VALUE = "qr_raw_value"
        private const val LAST_ITEM = "last_item"
        private const val PACKAGE_NAME = "com.google.ar.core"
        private const val MODE = "mode"
        private const val MODE_TYPE = "3d_only"
        private const val FILE = "file"
        private const val AR_URI = "https://arvr.google.com/scene-viewer/1.0"

        @JvmStatic
        fun newInstance(qrCodeRawValue: String?, lastSelectedItemPosition: Int) =
            QrCodeFragment().apply {
                arguments = Bundle().apply {
                    putString(QR_CODE_RAW_VALUE, qrCodeRawValue)
                    putInt(LAST_ITEM, lastSelectedItemPosition)
                }
            }
    }
}