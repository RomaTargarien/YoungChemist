package com.example.youngchemist.ui.screen.main.subjects.lectures.lectures_list

import android.animation.ObjectAnimator
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.youngchemist.databinding.ItemLectureInListBinding
import com.example.youngchemist.model.Lecture

class LecturesListAdapter : RecyclerView.Adapter<LecturesListAdapter.LectureViewHolder>() {

    private val differCallBack = object : DiffUtil.ItemCallback<Lecture>() {
        override fun areItemsTheSame(oldItem: Lecture, newItem: Lecture): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

        override fun areContentsTheSame(oldItem: Lecture, newItem: Lecture): Boolean {
            return oldItem.lectureId == newItem.lectureId
        }
    }

    private val differ = AsyncListDiffer(this, differCallBack)

    var lectures: List<Lecture>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    inner class LectureViewHolder(val binding: ItemLectureInListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Lecture) {
            binding.tvTitle.setText(item.lectureTitle)
            val pregressAnimator = ObjectAnimator.ofInt(binding.progressBar, "progress", 0, 50)
            pregressAnimator.duration = 1000
            pregressAnimator.start()
            binding.bnBeginLecture.setOnClickListener {
                onClick?.let { click ->
                    click(item)
                }
            }
            binding.bnBeginTest.setOnClickListener {
                onBeginTestButtonClicked?.let { click ->
                    click(item.testId)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LectureViewHolder {
        return LectureViewHolder(
            ItemLectureInListBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: LectureViewHolder, position: Int) {
        holder.bind(lectures[position])
    }

    override fun getItemCount() = lectures.size

    private var onBeginTestButtonClicked: ((String) -> Unit)? = null
    fun setOnBeginTestListener(listener: (String) -> Unit) {
        onBeginTestButtonClicked = listener
    }

    private var onClick: ((Lecture) -> Unit)? = null
    fun setOnClickListener(listener: (Lecture) -> Unit) {
        onClick = listener
    }
}