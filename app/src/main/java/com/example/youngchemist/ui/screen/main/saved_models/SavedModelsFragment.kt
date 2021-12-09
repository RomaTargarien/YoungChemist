package com.example.youngchemist.ui.screen.main.saved_models

import android.content.Intent
import android.graphics.Canvas
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionManager
import com.example.youngchemist.R
import com.example.youngchemist.databinding.FragmentSavedModelsBinding
import com.example.youngchemist.model.user.Model3D
import com.example.youngchemist.ui.screen.main.qr.qr_code.QrCodeFragment
import com.example.youngchemist.ui.screen.main.subjects.lectures.test.HorizontalItemDecoration
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SavedModelsFragment : Fragment() {

    private val viewModel: SavedModelsFragmentViewModel by viewModels()
    private lateinit var binding: FragmentSavedModelsBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        FragmentSavedModelsBinding.inflate(inflater,container,false).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val model3DAdapter = Model3DAdapter()
        binding.rv3DModels.apply {
            layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.VERTICAL,false)
            adapter = model3DAdapter
        }

        viewModel.model3DState.observe(viewLifecycleOwner,{
            model3DAdapter.differ.submitList(it)
            if (it.isEmpty()) {
                binding.tvEmptyList.isVisible = true
                if (binding.tvAmountOffModels.isVisible) {
                    TransitionManager.beginDelayedTransition(binding.mainContainer)
                    binding.tvAmountOffModels.isVisible = false
                }
            } else {
                TransitionManager.beginDelayedTransition(binding.mainContainer)
                binding.tvAmountOffModels.text = it.size.toString()
                binding.tvAmountOffModels.isVisible = true
            }
        })
        model3DAdapter.setOnClickListener {
            startActivity(createIntent(it.modelUri))
        }
//        model3DAdapter.setOnDeleteListener {
//            viewModel.deleteModel3D(it)
//        }
    }

    private fun createIntent(modelUrl: String): Intent {
        val intent = Intent(Intent.ACTION_VIEW)
        val uri = Uri.parse(AR_URI).buildUpon()
            .appendQueryParameter(FILE, modelUrl)
            .appendQueryParameter(MODE,MODE_TYPE)
            .build()
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.data = uri
        intent.setPackage(PACKAGE_NAME)
        return intent
    }

    companion object {
        private const val PACKAGE_NAME = "com.google.ar.core"
        private const val MODE = "mode"
        private const val MODE_TYPE = "3d_only"
        private const val FILE = "file"
        private const val AR_URI = "https://arvr.google.com/scene-viewer/1.0"
    }
}