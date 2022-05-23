package com.chemist.youngchemist.ui.screen.main.saved_models

import android.graphics.*
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
import com.chemist.youngchemist.R
import com.chemist.youngchemist.databinding.FragmentSavedModelsBinding
import com.chemist.youngchemist.model.user.Model3D
import com.chemist.youngchemist.ui.base.intent_3d.Intent3DCreator
import com.chemist.youngchemist.ui.custom.snack_bar.CustomSnackBar
import com.chemist.youngchemist.ui.custom.snack_bar.CustomSnackBar.Companion.setOnClickListener
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
        binding.rv3DModels.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            adapter = model3DAdapter
        }
        bitmap = BitmapFactory.decodeResource(resources, R.drawable.trash)

        viewModel.model3DState.observe(viewLifecycleOwner) {
            val list = it.first
            val state = it.second
            model3DAdapter.submitList(list)
            when (state) {
                is Query.Searching -> {
                    binding.tvEmptySearch.isVisible = list.isEmpty()
                    binding.tvAmountOffModels.text = list.size.toString()
                }
                is Query.All -> {
                    binding.tvEmptyList.isVisible = list.isEmpty()
                    animateTextNumberVisibility(list.size)
                }
            }
        }

        model3DAdapter.setOnClickListener {
            startActivity(Intent3DCreator.create3DIntent(it.modelUri))
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
}