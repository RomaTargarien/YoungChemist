package com.example.youngchemist.ui.screen.main.subjects.lectures.lectures_list

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.youngchemist.R
import com.example.youngchemist.databinding.ItemLectureInListBinding
import com.example.youngchemist.model.Test
import com.example.youngchemist.model.ui.LectureUi
import kotlin.math.roundToInt

class LecturesListAdapter : RecyclerView.Adapter<LecturesListAdapter.LectureViewHolder>() {

    private val lectures: MutableList<LectureUi> = mutableListOf()

    fun submitList(modelsList: List<LectureUi>) {
        val result: DiffUtil.DiffResult = DiffUtil.calculateDiff(DiffCallback(lectures, modelsList))
        result.dispatchUpdatesTo(this)
        Log.d("TAG","list_submitted")
        lectures.clear()
        lectures.addAll(modelsList)
        notifyDataSetChanged()
    }

    inner class LectureViewHolder(val binding: ItemLectureInListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: LectureUi) {
            binding.tvTitle.text = item.lectureTitle
            binding.tvDescription.text = item.lectureDescription
            binding.bnBeginTest.isVisible = false
            binding.tvTextTestMark.isVisible = false
            binding.tvTestMark.isVisible = false
            item.test?.let {
                toggleTestState(item.isTestEnabled)
            }
            item.userProgress?.let {
                val to = ((it.lastReadenPage.toFloat() / item.data.size.toFloat()) * 100).toInt()
                val pregressAnimator = ObjectAnimator.ofInt(binding.pbLecture, "progress", 0, to)
                pregressAnimator.duration = 1800
                pregressAnimator.start()
            }
            val animator = ValueAnimator.ofFloat(0.0f, item.mark.toFloat())
            animator.duration = 2000
            animator.addUpdateListener(object : ValueAnimator.AnimatorUpdateListener {
                override fun onAnimationUpdate(p0: ValueAnimator?) {
                    val number = (p0?.animatedValue as Float).toDouble()
                    val roundedNumber: Double = (number * 10.0).roundToInt() / 10.0
                    binding.tvTestMark.text = roundedNumber.toString()
                }
            })
            animator.start()
            binding.cvLecture.setOnClickListener {
                onClick?.let { click ->
                    click(item)
                }
            }
            binding.bnBeginTest.setOnClickListener {
                onBeginTestButtonClicked?.let { click ->
                    item.test?.let { click(it) }
                }
            }
        }

        private fun toggleTestState(isTestEnabled: Boolean) {
            if (isTestEnabled) {
                binding.bnBeginTest.isVisible = true
            } else {
                binding.tvTextTestMark.isVisible = true
                binding.tvTestMark.isVisible = true
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
        val animation = AnimationUtils.loadAnimation(holder.itemView.context, R.anim.alpha_anim)
        holder.binding.clMain.startAnimation(animation)
    }

    override fun getItemCount() = lectures.size

    private var onBeginTestButtonClicked: ((Test) -> Unit)? = null
    fun setOnBeginTestListener(listener: (Test) -> Unit) {
        onBeginTestButtonClicked = listener
    }

    private var onClick: ((LectureUi) -> Unit)? = null
    fun setOnClickListener(listener: (LectureUi) -> Unit) {
        onClick = listener
    }
}

class DiffCallback(private val oldList: List<LectureUi>, private val newList: List<LectureUi>) :
    DiffUtil.Callback() {

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
        return (oldList[oldItemPosition].lectureId == newList[newItemPosition].lectureId)
                && (oldList[oldItemPosition].userProgress?.lastReadenPage == newList[newItemPosition].userProgress?.lastReadenPage)
    }
}