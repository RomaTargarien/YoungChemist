package com.chemist.youngchemist.ui.screen.main.subjects.lectures.lectures_list

import android.animation.ValueAnimator
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.chemist.youngchemist.R
import com.chemist.youngchemist.databinding.ItemLectureInListBinding
import com.chemist.youngchemist.model.Test
import com.chemist.youngchemist.model.ui.LectureUi
import com.chemist.youngchemist.model.user.UserProgress
import com.chemist.youngchemist.ui.util.animateProgress
import kotlin.math.roundToInt


class LecturesListAdapter : RecyclerView.Adapter<LecturesListAdapter.LectureViewHolder>() {

    private val lectures: MutableList<LectureUi> = mutableListOf()

    fun submitList(lecturesList: List<LectureUi>) {
        val result: DiffUtil.DiffResult = DiffUtil.calculateDiff(DiffCallback(lectures, lecturesList))
        result.dispatchUpdatesTo(this)
        lectures.clear()
        lectures.addAll(lecturesList)
        notifyDataSetChanged()
    }

    inner class LectureViewHolder(val binding: ItemLectureInListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: LectureUi,position: Int) {
            binding.tvTitle.text = item.lectureTitle
            binding.tvDescription.text = item.lectureDescription
            binding.bnBeginTest.isVisible = false
            binding.tvTextTestMark.isVisible = false
            binding.tvTestMark.isVisible = false
            item.test?.let {
                toggleTestState(item.isTestEnabled)
            }
            item.userProgress?.let {
                toggleUserProgressState(it,item)
            }
            setOnClickListeners(item)
        }

        private fun toggleTestState(isTestEnabled: Boolean) {
            if (isTestEnabled) {
                binding.bnBeginTest.isVisible = true
            } else {
                binding.tvTextTestMark.isVisible = true
                binding.tvTestMark.isVisible = true
            }
        }

        private fun toggleUserProgressState(userProgress: UserProgress,lectureUi: LectureUi) {
            val to = ((userProgress.lastReadenPage.toFloat() / lectureUi.data.size.toFloat()) * 100).toInt()
            binding.pbLecture.animateProgress(0,to)
            val animator = ValueAnimator.ofFloat(0.0f, lectureUi.mark.toFloat())
            animator.duration = 2000
            animator.addUpdateListener(object : ValueAnimator.AnimatorUpdateListener {
                override fun onAnimationUpdate(p0: ValueAnimator?) {
                    val number = (p0?.animatedValue as Float).toDouble()
                    val roundedNumber: Double = (number * 10.0).roundToInt() / 10.0
                    binding.tvTestMark.text = roundedNumber.toString()
                }
            })
            animator.start()
        }

        private fun setOnClickListeners(lectureUi: LectureUi) {
            binding.cvLecture.setOnClickListener {
                lectureUi.userProgress?.let {
                    onClick?.let { click ->
                        click(lectureUi)
                    }
                }
            }

            binding.bnBeginTest.setOnClickListener {
                lectureUi.userProgress?.let {
                    onBeginTestButtonClicked?.let { click ->
                        lectureUi.test?.let { click(it) }
                    }
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
        holder.bind(lectures[position],position)
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

    inner class DiffCallback(private val oldList: List<LectureUi>, private val newList: List<LectureUi>) :
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
}

