package com.chemist.youngchemist.ui.screen.main.subjects

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.chemist.youngchemist.databinding.ItemSubjectBinding
import com.chemist.youngchemist.model.Subject

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
            binding.subject = item
            binding.containerItem.alpha = if (isClickable) 1f else 0.5f
            binding.ivSubject.setOnClickListener {
                onClick?.let { click ->
                    if (isClickable) {
                        click(item)
                    }
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