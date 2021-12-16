package com.example.youngchemist.ui.screen.main.subjects

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.youngchemist.databinding.ItemSubjectBinding
import com.example.youngchemist.model.Subject
import com.example.youngchemist.ui.util.BitmapUtils

class SubjectsAdapter : RecyclerView.Adapter<SubjectsAdapter.SubjectViewHolder>() {

    var isClickable: Boolean = false

    private val differCallBack = object : DiffUtil.ItemCallback<Subject>() {
        override fun areItemsTheSame(oldItem: Subject, newItem: Subject): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

        override fun areContentsTheSame(oldItem: Subject, newItem: Subject): Boolean {
            return oldItem.icon_url == newItem.icon_url
        }

    }

    private val differ = AsyncListDiffer(this, differCallBack)

    var subjects: List<Subject>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    inner class SubjectViewHolder(val binding: ItemSubjectBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Subject) {
            if (!isClickable) {
                binding.containerItem.alpha = 0.5f
            } else {
                binding.containerItem.alpha = 1f
            }
            binding.title.setText(item.title)
            val bitmap = BitmapUtils.convertCompressedByteArrayToBitmap(item.iconByteArray)
            binding.ivSubject.setImageBitmap(bitmap)
            binding.ivSubject.setOnClickListener {
                onClick?.let { click ->
                    if (isClickable) {click(item)}
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectViewHolder {
        return SubjectViewHolder(
            ItemSubjectBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: SubjectViewHolder, position: Int) {
        holder.bind(subjects[position])
    }

    private var onClick: ((Subject) -> Unit)? = null

    fun setOnClickListener(listener: (Subject) -> Unit) {
        onClick = listener
    }


    override fun getItemCount() = subjects.size
}