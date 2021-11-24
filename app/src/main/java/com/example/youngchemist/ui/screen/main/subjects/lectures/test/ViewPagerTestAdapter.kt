package com.example.youngchemist.ui.screen.main.subjects.lectures.test

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.youngchemist.databinding.ItemTestBinding
import com.example.youngchemist.model.Subject
import com.example.youngchemist.model.Task
import com.ms.square.android.expandabletextview.ExpandableTextView

class ViewPagerTestAdapter: RecyclerView.Adapter<ViewPagerTestAdapter.ViewPagerTestViewHolder>()  {

    private val differCallBack = object : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

    }

    private val differ = AsyncListDiffer(this,differCallBack)

    var tasks: List<Task>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    inner class ViewPagerTestViewHolder(val binding: ItemTestBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Task) {
            binding.tvQuestion.text = item.question
            val testAnswerAdapter = TestAnswerAdapter()
            binding.rvAnswers.layoutManager = LinearLayoutManager(itemView.context,LinearLayoutManager.VERTICAL,false)
            binding.rvAnswers.addItemDecoration(HorizontalItemDecoration(25))
            binding.rvAnswers.adapter = testAnswerAdapter
            testAnswerAdapter.answers = item.answers
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewPagerTestViewHolder(ItemTestBinding.inflate(LayoutInflater.from(parent.context),parent,false))

    override fun onBindViewHolder(holder: ViewPagerTestViewHolder, position: Int) {
        holder.bind(tasks[position])
    }

    override fun getItemCount() = tasks.size
}