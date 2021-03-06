package com.chemist.youngchemist.ui.screen.main.qr.qr_code

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.chemist.youngchemist.R
import com.chemist.youngchemist.databinding.FragmentQrCodeScannerBinding
import com.chemist.youngchemist.ui.base.intent_3d.Intent3DCreator
import com.chemist.youngchemist.ui.util.ResourceNetwork
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.FlowPreview

@FlowPreview
@AndroidEntryPoint
class QrCodeFragment : Fragment() {

    private val viewModel: QrCodeFragmentViewModel by viewModels()
    private lateinit var binding: FragmentQrCodeScannerBinding
    private var modelId: String? = null
    private var lastSelectedItemPosition: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            modelId = it.getString(MODEL_ID)
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
        viewModel.lastSelectedItemPosition = lastSelectedItemPosition
        toggleButtonsBehavior(false)
        requireActivity().onBackPressedDispatcher.addCallback {
            viewModel.exit()
        }
        modelId?.let {
            viewModel.get3DModel(it)
        }

        viewModel.model3DState.observe(viewLifecycleOwner) {
            when (it) {
                is ResourceNetwork.Loading -> {
                    binding.progressFlask.isVisible = true
                }
                is ResourceNetwork.Success -> {
                    binding.progressFlask.isVisible = false
                    toggleButtonsBehavior(true)
                    it.data?.let { model ->
                        binding.bnOpen.setOnClickListener {
                            startActivity(Intent3DCreator.create3DIntent(model.modelUri))
                        }
                        binding.tvTitle.text = model.modelTitle
                    }
                }
                is ResourceNetwork.Error -> {
                    binding.progressFlask.isVisible = false
                    binding.tvErrorText.isVisible = true
                    binding.tvTryAgain.isVisible = true
                }
            }
        }
        viewModel.wasSaved.observe(viewLifecycleOwner) {
            if (it) {
                toggleSaveButtonState()
            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun toggleSaveButtonState() {
        binding.bnSave.background = resources.getDrawable(R.drawable.shape_outlined_rectangle_for_saving_model)
        binding.ivSave.setImageResource(R.drawable.ic_icon_done)
        binding.bnSave.isEnabled = false
        binding.bnSave.alpha = 0.7f
    }

    private fun toggleButtonsBehavior(enable: Boolean) {
        binding.bnSave.alpha = if (enable) 1f else 0.5f
        binding.bnOpen.alpha = if (enable) 1f else 0.5f
        binding.iv3d.alpha = if (enable) 1f else 0.5f
        binding.iv3d.alpha = if (enable) 1f else 0.5f
        binding.bnSave.isEnabled = enable
        binding.bnOpen.isEnabled = enable
    }

    companion object {
        private const val MODEL_ID = "qr_raw_value"
        private const val LAST_ITEM = "last_item"

        @JvmStatic
        fun newInstance(modelId: String?, lastSelectedItemPosition: Int) =
            QrCodeFragment().apply {
                arguments = Bundle().apply {
                    putString(MODEL_ID, modelId)
                    putInt(LAST_ITEM, lastSelectedItemPosition)
                }
            }
    }
}