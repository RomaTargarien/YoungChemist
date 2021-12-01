package com.example.youngchemist.ui.screen.main.subjects.lectures.test

import android.annotation.SuppressLint
import android.content.res.Resources
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.children
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.youngchemist.R
import com.example.youngchemist.databinding.ItemAnswerBinding
import com.example.youngchemist.model.Answer
import com.example.youngchemist.model.AnswerUi

class TestAnswerAdapter(
    private val multipleAnswersAvailable: Boolean,
    private val resourses: Resources,
    private val answersUi: List<AnswerUi>
) : RecyclerView.Adapter<TestAnswerAdapter.TestAnswerViewHolder>() {

    private val differCallBack = object : DiffUtil.ItemCallback<Answer>() {
        override fun areItemsTheSame(oldItem: Answer, newItem: Answer): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

        override fun areContentsTheSame(oldItem: Answer, newItem: Answer): Boolean {
            return oldItem.text == newItem.text
        }
    }
    private var previousAnswerNumber: Int = -1

    private val differ = AsyncListDiffer(this, differCallBack)

    private val mapBinding: MutableMap<Int, ItemAnswerBinding> = mutableMapOf()

    var answers: List<Answer>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    init {
        if (!multipleAnswersAvailable) {
            for (item in answersUi) {
                if (item.itIsRight) {
                    previousAnswerNumber = item.position
                }
            }
        }
    }

    inner class TestAnswerViewHolder(val binding: ItemAnswerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(answer: Answer, position: Int) {
            if (answersUi[position].itIsRight) {
                handleUnselectedAnswer(position, binding.cvAnswer, false)
            }
            binding.tvAnswerNumber.text = "${position + 1})"
            binding.tvAnswer.text = answer.text
            binding.cvAnswer.setOnClickListener {
                handleAnswer(binding.cvAnswer, position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestAnswerViewHolder {
        return TestAnswerViewHolder(
            ItemAnswerBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: TestAnswerViewHolder, position: Int) {
        mapBinding[position] = holder.binding
        mapBinding.filterValues { it == holder.binding }.keys
            .toList()
            .filter { it != position }
            .forEach {
                mapBinding.remove(it)
            }
        holder.bind(answers[position], position)
    }

    override fun getItemCount() = answers.size

    private fun handleAnswer(layout: ConstraintLayout, position: Int) {
        if (answersUi[position].itIsRight) {
            handleSelectedAnswer(position, layout)
        } else {
            handleUnselectedAnswer(position, layout)
            if (previousAnswerNumber >= 0 && position != previousAnswerNumber && !multipleAnswersAvailable) {
                handleSelectedAnswer(
                    previousAnswerNumber,
                    mapBinding[previousAnswerNumber]?.cvAnswer
                )
            }
        }
        previousAnswerNumber = position
    }

    private var onClick: ((List<AnswerUi>) -> Unit)? = null
    fun setOnClickListener(listener: (List<AnswerUi>) -> Unit) {
        onClick = listener
    }

    private fun handleAnswerClick(answerPosition: Int, value: Boolean) {
        answersUi[answerPosition].itIsRight = value
        onClick?.let { click ->
            click(answersUi)
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun handleSelectedAnswer(position: Int, layout: ConstraintLayout?) {
        layout?.children?.forEach { (it as TextView).setTextColor(Color.BLACK) }
        layout?.background = resourses.getDrawable(R.drawable.shape_rounded_for_unselected_test)
        handleAnswerClick(position, false)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun handleUnselectedAnswer(
        position: Int,
        layout: ConstraintLayout,
        answerClick: Boolean = true
    ) {
        layout.background = resourses.getDrawable(R.drawable.shape_rounded_for_selected_test)
        layout.children.forEach { (it as TextView).setTextColor(Color.WHITE) }
        if (answerClick) {
            handleAnswerClick(position, true)
        }
    }
}