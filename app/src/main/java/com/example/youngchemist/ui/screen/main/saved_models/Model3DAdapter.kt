package com.example.youngchemist.ui.screen.main.saved_models

import android.util.Log
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

//    private val models3D = mutableListOf<Model3D>()
//
//    fun addList(models: List<Model3D>) {
//        models3D.addAll(models)
//
//    }


    private val differCallBack = object : DiffUtil.ItemCallback<Model3D>() {
        override fun areItemsTheSame(oldItem: Model3D, newItem: Model3D): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

        override fun areContentsTheSame(oldItem: Model3D, newItem: Model3D): Boolean {
            return oldItem == newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallBack)

    inner class Model3DViewHoler(val binding: Item3dBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Model3D,position: Int) {
            binding.tvTitle.text = item.modelTitle
            binding.cvModel.setOnClickListener {
                onClick?.let { click ->
                    click(item)
                }
            }
            binding.ivDelete.setOnClickListener{
                Log.d("TAG","click")
                onDeleteClick?.let { click ->
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
        holder.bind(differ.currentList[position],position)
//        val animation = AnimationUtils.loadAnimation(holder.itemView.context, R.anim.alpha_anim)
//        holder.binding.clMain.startAnimation(animation)
    }

    override fun getItemCount() = differ.currentList.size

    private var onClick: ((Model3D) -> Unit)? = null
    fun setOnClickListener(listener: (Model3D) -> Unit) {
        onClick = listener
    }

    private var onDeleteClick: ((Model3D) -> Unit)? = null
    fun setOnDeleteListener(listener: (Model3D) -> Unit) {
        onDeleteClick = listener
    }
}