package com.example.youngchemist.ui.screen.main.saved_models

import android.content.Intent
import android.graphics.*
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.TransitionManager
import com.example.youngchemist.R
import com.example.youngchemist.databinding.FragmentSavedModelsBinding
import com.example.youngchemist.model.user.Model3D
import com.example.youngchemist.ui.custom.snack_bar.CustomSnackBar
import com.example.youngchemist.ui.custom.snack_bar.CustomSnackBar.Companion.setOnClickListener
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SavedModelsFragment : Fragment() {

    private val viewModel: SavedModelsFragmentViewModel by viewModels()
    private lateinit var binding: FragmentSavedModelsBinding
    private lateinit var model3DAdapter: Model3DAdapter
    private lateinit var bitmap: Bitmap

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        FragmentSavedModelsBinding.inflate(inflater, container, false).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        model3DAdapter = Model3DAdapter()
        binding.viewModel = viewModel
        enableSwipe()
        viewModel.start()
        binding.rv3DModels.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = model3DAdapter
        }
        bitmap = BitmapFactory.decodeResource(resources, R.drawable.trash)

        viewModel.model3DState.observe(viewLifecycleOwner, {
            val list = it.first
            val state = it.second
            model3DAdapter.submitList(list)
            when (state) {
                is Query.Searching -> {
                    binding.tvEmptySearch.isVisible = list.isEmpty()
                }
                is Query.All -> {
                    binding.tvEmptySearch.isVisible = false
                    animateTextNumberVisibility(list.size)
                }
            }

        })

        model3DAdapter.setOnClickListener {
            startActivity(createIntent(it.modelUri))
        }

    }

    private fun enableSwipe() {
        val simpleItemTouchHelper =
            object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val position = viewHolder.adapterPosition
                    if (direction == ItemTouchHelper.LEFT) {
                        val model = model3DAdapter.getItem(position)
                        undo(model)
                        viewModel.deleteModel3D(model)
                    }
                }

                override fun onChildDraw(
                    c: Canvas,
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    dX: Float,
                    dY: Float,
                    actionState: Int,
                    isCurrentlyActive: Boolean
                ) {
                    super.onChildDraw(
                        c,
                        recyclerView,
                        viewHolder,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )
                    val p = Paint()
                    if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {
                        val itemView = viewHolder.itemView
                        val height = itemView.bottom.toFloat() - itemView.top.toFloat()
                        val width = height / 3
                        if (dX < 0) {
                            val iconDest = RectF(
                                itemView.right.toFloat() - 2 * width,
                                itemView.top.toFloat() + width,
                                itemView.right.toFloat() - width,
                                itemView.bottom.toFloat() - width
                            )
                            val alpha = ((-dX / itemView.width.toFloat()) * 255).toInt()
                            p.alpha = alpha
                            c.drawBitmap(bitmap, null, iconDest, p)
                        }
                    }
                }
            }
        val itemTouchHelper = ItemTouchHelper(simpleItemTouchHelper)
        itemTouchHelper.attachToRecyclerView(binding.rv3DModels)
    }

    private fun animateTextNumberVisibility(size: Int) {
        if (size == 0) {
            if (binding.tvAmountOffModels.isVisible) {
                TransitionManager.beginDelayedTransition(binding.mainContainer)
                binding.tvAmountOffModels.isVisible = false
                binding.tvEmptyList.isVisible = true
                binding.searchingContainer.isVisible = false
            }
        } else {
            if (!binding.tvAmountOffModels.isVisible) {
                TransitionManager.beginDelayedTransition(binding.mainContainer)
                binding.tvAmountOffModels.isVisible = true
                binding.searchingContainer.isVisible = true
                binding.tvEmptyList.isVisible = false
            }
            binding.tvAmountOffModels.text = size.toString()
        }
    }

    private fun createIntent(modelUrl: String): Intent {
        val intent = Intent(Intent.ACTION_VIEW)
        val uri = Uri.parse(AR_URI).buildUpon()
            .appendQueryParameter(FILE, modelUrl)
            .appendQueryParameter(MODE, MODE_TYPE)
            .build()
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.data = uri
        intent.setPackage(PACKAGE_NAME)
        return intent
    }

    private fun undo(model3D: Model3D) {
        CustomSnackBar.make(activity?.window?.decorView?.rootView as ViewGroup,model3D.modelTitle)
            .setOnClickListener {
                viewModel.undoDelete(model3D)
                it.dismiss()
            }
            .setAnchorView(binding.snackbarAnchor)
            .setDuration(3000)
            .show()
    }

    override fun onPause() {
        super.onPause()
        Log.d("TAG", "pause")
        viewModel.cancel()
    }

    companion object {
        private const val PACKAGE_NAME = "com.google.ar.core"
        private const val MODE = "mode"
        private const val MODE_TYPE = "3d_only"
        private const val FILE = "file"
        private const val AR_URI = "https://arvr.google.com/scene-viewer/1.0"
    }
}