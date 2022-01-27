package com.example.youngchemist.ui.screen.main.saved_models

import android.util.Log
import android.view.Display
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.youngchemist.R
import com.example.youngchemist.databinding.Item3dBinding
import com.example.youngchemist.model.user.Model3D


class Model3DAdapter : RecyclerView.Adapter<Model3DAdapter.Model3DViewHoler>() {

    private val models: MutableList<Model3D> = mutableListOf()

    fun submitList(modelsList: List<Model3D>) {
        val result: DiffUtil.DiffResult = DiffUtil.calculateDiff(DiffCallback(models,modelsList))
        result.dispatchUpdatesTo(this)
        models.clear()
        models.addAll(modelsList)
    }

    fun getItem(position: Int) = models[position]

    inner class Model3DViewHoler(val binding: Item3dBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Model3D) {
            binding.tvTitle.text = item.modelTitle
            binding.tvDateOffAdding.text = item.addingDate
            binding.cvModel.setOnClickListener {
                onClick?.let { click ->
                    click(item)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Model3DViewHoler {
        return Model3DViewHoler(
            Item3dBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        )
    }

    override fun onBindViewHolder(holder: Model3DViewHoler, position: Int) {
        holder.bind(models[position])
        val animation = AnimationUtils.loadAnimation(holder.itemView.context, R.anim.alpha_anim)
        holder.binding.clMain.startAnimation(animation)
    }

    override fun getItemCount() = models.size

    private var onClick: ((Model3D) -> Unit)? = null
    fun setOnClickListener(listener: (Model3D) -> Unit) {
        onClick = listener
    }

    inner class DiffCallback(private val oldList: List<Model3D>,private val newList: List<Model3D>): DiffUtil.Callback() {

        override fun getOldListSize(): Int {
            return oldList.size
        }

        override fun getNewListSize(): Int {
            return newList.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].hashCode() == newList[newItemPosition].hashCode()
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].modelId == newList[newItemPosition].modelId
        }
    }
}

