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
    private val resourses: Resources
) : RecyclerView.Adapter<TestAnswerAdapter.TestAnswerViewHolder>() {

    private val differCallBack = object : DiffUtil.ItemCallback<Answer>() {
        override fun areItemsTheSame(oldItem: Answer, newItem: Answer): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

        override fun areContentsTheSame(oldItem: Answer, newItem: Answer): Boolean {
            return oldItem.text == newItem.text
        }
    }

    private var onClick: ((MutableList<AnswerUi>) -> Unit)? = null
    fun setOnClickListener(listener: (MutableList<AnswerUi>) -> Unit) {
        onClick = listener
    }

    private val differ = AsyncListDiffer(this, differCallBack)
    private var previousAnswerNumber: Int = -1

    private val mapBinding: MutableMap<Int, ItemAnswerBinding> = mutableMapOf()
    private val wasAnswered: MutableMap<Int, Boolean> = mutableMapOf()
    private val answerUiList: MutableList<AnswerUi> = mutableListOf()

    var answers: List<Answer>
        get() = differ.currentList
        set(value) = differ.submitList(value).also {
            for (i in 0..value.size - 1) {
                wasAnswered[i] = false
                answerUiList.add(AnswerUi(i, false))
            }
            onClick?.let { click ->
                click(answerUiList)
            }
        }

    inner class TestAnswerViewHolder(val binding: ItemAnswerBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(answer: Answer, position: Int) {
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
        wasAnswered[position]?.let {
            if (it) {
                handleSelectedAnswer(position, layout)
            } else {
                handleUnselectedAnswer(position, layout)
                if (previousAnswerNumber >= 0 && position != previousAnswerNumber && !multipleAnswersAvailable) {
                    handleSelectedAnswer(previousAnswerNumber,mapBinding[previousAnswerNumber]?.cvAnswer)
                }
            }
            previousAnswerNumber = position
        }
    }

    private fun handleAnswerClick(answerPosition: Int,value: Boolean) {
        answerUiList[answerPosition].itIsRight = value
        onClick?.let { click -> click(answerUiList) }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun handleSelectedAnswer(position: Int, layout: ConstraintLayout?) {
        layout?.children?.forEach { (it as TextView).setTextColor(Color.BLACK) }
        layout?.background = resourses.getDrawable(R.drawable.shape_rounded_for_unselected_test)
        wasAnswered[position] = false
        handleAnswerClick(position,false)
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private fun handleUnselectedAnswer(position: Int, layout: ConstraintLayout) {
        layout.background = resourses.getDrawable(R.drawable.shape_rounded_for_selected_test)
        layout.children.forEach { (it as TextView).setTextColor(Color.WHITE) }
        wasAnswered[position] = true
        handleAnswerClick(position,true)
    }
}