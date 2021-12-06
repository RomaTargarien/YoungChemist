package com.example.youngchemist.ui.screen.main.subjects.lectures.lectures_list

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.view.isVisible
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.youngchemist.R
import com.example.youngchemist.databinding.ItemLectureInListBinding
import com.example.youngchemist.model.Lecture
import com.example.youngchemist.model.LectureUi

class LecturesListAdapter : RecyclerView.Adapter<LecturesListAdapter.LectureViewHolder>() {

    private val differCallBack = object : DiffUtil.ItemCallback<LectureUi>() {
        override fun areItemsTheSame(oldItem: LectureUi, newItem: LectureUi): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

        override fun areContentsTheSame(oldItem: LectureUi, newItem: LectureUi): Boolean {
            return oldItem.lectureId == newItem.lectureId
        }
    }

    private val differ = AsyncListDiffer(this, differCallBack)

    var lectures: List<LectureUi>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    inner class LectureViewHolder(val binding: ItemLectureInListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: LectureUi) {
            binding.tvTitle.text = item.lectureTitle
            binding.tvDescription.text = item.lectureDescription
            binding.bnBeginTest.alpha = 0.7f
            binding.bnBeginTest.isEnabled = false
            binding.bnBeginTest.isVisible = true
            if (item.lectureWasReaden) {
                val pregressAnimator = ObjectAnimator.ofInt(binding.pbLecture, "progress", 0, 100)
                pregressAnimator.duration = 1800
                pregressAnimator.start()
            }
            if (item.testId.isEmpty()) {
                binding.bnBeginTest.isVisible = false
            }
            Log.d("TAG",item.testId + " " + item.isTestEnabled + " " + item.mark)
            if (item.isTestEnabled) {
                binding.bnBeginTest.isEnabled = true
                binding.bnBeginTest.alpha = 1f
            }
            val animator = ValueAnimator.ofFloat(0.0f,item.mark.toFloat())
            animator.duration = 2000
            animator.addUpdateListener(object : ValueAnimator.AnimatorUpdateListener {
                override fun onAnimationUpdate(p0: ValueAnimator?) {
                    val number = (p0?.animatedValue as Float).toDouble()
                    val roundedNumber: Double = Math.round(number*10.0)/10.0
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
        val animation = AnimationUtils.loadAnimation(holder.itemView.context, R.anim.alpha_anim)
        holder.binding.clMain.startAnimation(animation)
    }

    override fun getItemCount() = lectures.size

    private var onBeginTestButtonClicked: ((String) -> Unit)? = null
    fun setOnBeginTestListener(listener: (String) -> Unit) {
        onBeginTestButtonClicked = listener
    }

    private var onClick: ((LectureUi) -> Unit)? = null
    fun setOnClickListener(listener: (LectureUi) -> Unit) {
        onClick = listener
    }
}