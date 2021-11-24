package com.example.youngchemist.ui.screen.main.subjects.lectures.test

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.youngchemist.databinding.ItemAnswerBinding
import com.example.youngchemist.databinding.ItemPageNumberBinding
import com.example.youngchemist.model.Answer
import com.example.youngchemist.model.Task

class TestAnswerAdapter : RecyclerView.Adapter<TestAnswerAdapter.TestAnswerViewHolder>() {

    private val differCallBack = object : DiffUtil.ItemCallback<Answer>() {
        override fun areItemsTheSame(oldItem: Answer, newItem: Answer): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

        override fun areContentsTheSame(oldItem: Answer, newItem: Answer): Boolean {
            return oldItem.text == newItem.text
        }

    }

    private val differ = AsyncListDiffer(this,differCallBack)

    var answers: List<Answer>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    inner class TestAnswerViewHolder(private val binding: ItemAnswerBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(answer: Answer) {
            binding.tvAnswer.text= answer.text
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestAnswerViewHolder {
        return TestAnswerViewHolder(ItemAnswerBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: TestAnswerViewHolder, position: Int) {
        holder.bind(answers[position])
    }

    override fun getItemCount() = answers.size
}