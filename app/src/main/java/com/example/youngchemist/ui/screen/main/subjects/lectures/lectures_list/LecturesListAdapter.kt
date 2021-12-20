package com.example.youngchemist.ui.screen.main.subjects.lectures.lectures_list

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.youngchemist.R
import com.example.youngchemist.databinding.ItemAnswerBinding
import com.example.youngchemist.databinding.ItemLectureInListBinding
import com.example.youngchemist.model.Test
import com.example.youngchemist.model.ui.LectureUi
import com.example.youngchemist.model.user.UserProgress
import com.example.youngchemist.ui.util.animateProgress
import com.example.youngchemist.ui.util.shake
import kotlin.math.roundToInt

class LecturesListAdapter : RecyclerView.Adapter<LecturesListAdapter.LectureViewHolder>() {

    private val lectures: MutableList<LectureUi> = mutableListOf()

    private val mapBinding: MutableMap<Int, ItemLectureInListBinding> = mutableMapOf()
    private var previousSelectedPosition: Int? = null

    fun submitList(modelsList: List<LectureUi>) {
        val result: DiffUtil.DiffResult = DiffUtil.calculateDiff(DiffCallback(lectures, modelsList))
        result.dispatchUpdatesTo(this)
        lectures.clear()
        lectures.addAll(modelsList)
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
            setOnClickListeners(item,position)

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
            if (userProgress.isLectureEnabled) {
                binding.ivLectureAvailable.setImageResource(R.drawable.success)
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
        }

        private fun setOnClickListeners(lectureUi: LectureUi,position: Int) {
            binding.cvLecture.setOnClickListener {
                lectureUi.userProgress?.let {
                    if (it.isLectureEnabled) {
                        onClick?.let { click ->
                            click(lectureUi)
                        }
                    } else {
                        binding.ivLectureAvailable.shake().start()
                    }
                }
            }

            binding.bnBeginTest.setOnClickListener {
                lectureUi.userProgress?.let {
                    if (it.isLectureEnabled) {
                        onBeginTestButtonClicked?.let { click ->
                            lectureUi.test?.let { click(it) }
                        }
                    }
                }
            }
            binding.ivLecture.setOnClickListener {
                lectureUi.userProgress?.let {
                    if (!it.isLectureEnabled) {
                        if (position != previousSelectedPosition && previousSelectedPosition != null) {
                            mapBinding[previousSelectedPosition]?.clMain?.transitionToState(R.id.start)
                            previousSelectedPosition = null
                        }
                        if (position == previousSelectedPosition) {
                            binding.clMain.transitionToState(R.id.start)
                            previousSelectedPosition = null
                        } else {
                            binding.clMain.transitionToState(R.id.end)
                            previousSelectedPosition = position
                        }
                    }
                }
            }

            binding.ivEnterKey.setOnClickListener {
                if (binding.etKey.text.toString() == lectureUi.lectureKeyWord) {
                    onLectureIsUnlockedClicked?.let { click ->
                        lectureUi.userProgress?.isLectureEnabled = true
                        click(lectureUi)
                    }
                } else {
                    binding.ivEnterKey.shake().start()
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
        mapBinding[position] = holder.binding
        mapBinding.filterValues { it == holder.binding }.keys
            .toList()
            .filter { it != position }
            .forEach {
                mapBinding.remove(it)
            }
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

    private var onLectureIsUnlockedClicked: ((LectureUi)-> Unit)? = null
    fun setOnLectureIsUnlockedListener(listener: (LectureUi) -> Unit) {
        onLectureIsUnlockedClicked = listener
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