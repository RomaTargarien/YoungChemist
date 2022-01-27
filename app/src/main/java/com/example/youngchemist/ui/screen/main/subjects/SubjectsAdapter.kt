package com.example.youngchemist.ui.screen.main.subjects

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.youngchemist.R
import com.example.youngchemist.databinding.ItemSubjectBinding
import com.example.youngchemist.model.Subject
import com.example.youngchemist.ui.util.BitmapUtils
import com.squareup.picasso.Picasso

class SubjectsAdapter : RecyclerView.Adapter<SubjectsAdapter.SubjectViewHolder>() {

    var isClickable: Boolean = false

    private val subjects: MutableList<Subject> = mutableListOf()

    private var onClick: ((Subject) -> Unit)? = null
    fun setOnClickListener(listener: (Subject) -> Unit) {
        onClick = listener
    }

    fun submitList(subjectsList: List<Subject>) {
        val result: DiffUtil.DiffResult =
            DiffUtil.calculateDiff(DiffCallback(subjects, subjectsList))
        result.dispatchUpdatesTo(this)
        subjects.clear()
        subjects.addAll(subjectsList)
    }

    inner class SubjectViewHolder(val binding: ItemSubjectBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Subject) {
            if (!isClickable) {
                binding.containerItem.alpha = 0.5f
            } else {
                binding.containerItem.alpha = 1f
            }
            binding.title.setText(item.title)
            loadImage(item)
            binding.ivSubject.setOnClickListener {
                onClick?.let { click ->
                    if (isClickable) {
                        click(item)
                    }
                }
            }
        }

        private fun loadImage(item: Subject) {
            if (item.iconByteArray.isEmpty()) {
                Picasso.get()
                    .load(item.icon_url)
                    .placeholder(R.drawable.ic_icon_happy_flask)
                    .into(binding.ivSubject)
            } else {
                val bitmap = BitmapUtils.convertCompressedByteArrayToBitmap(item.iconByteArray)
                binding.ivSubject.setImageBitmap(bitmap)
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

    override fun getItemCount() = subjects.size

    inner class DiffCallback(
        private val oldList: List<Subject>,
        private val newList: List<Subject>
    ) : DiffUtil.Callback() {

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
            return oldList[oldItemPosition].subjectId == newList[newItemPosition].subjectId
        }
    }
}