package com.example.youngchemist.ui.screen.main.subjects.lectures.test

import android.content.res.Resources
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.youngchemist.R
import com.example.youngchemist.databinding.ItemTestBinding
import com.example.youngchemist.model.Task
import com.example.youngchemist.ui.listeners.OnTaskAnsweredTestListener
import com.squareup.picasso.Picasso

class ViewPagerTestAdapter(
    private val resources: Resources
) : RecyclerView.Adapter<ViewPagerTestAdapter.ViewPagerTestViewHolder>() {

    private lateinit var listener: OnTaskAnsweredTestListener
    fun setListener(onTaskAnsweredTestListener: OnTaskAnsweredTestListener) {
        listener = onTaskAnsweredTestListener
    }
    private val differCallBack = object : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }


    private val differ = AsyncListDiffer(this, differCallBack)

    var tasks: List<Task>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    inner class ViewPagerTestViewHolder(val binding: ItemTestBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Task,position: Int) {
            binding.tvQuestion.text = "${position+1}) ${item.question}"
            if (item.imageToQuestionUrl.isNotEmpty()) {
                binding.ivQuestion.load(item.imageToQuestionUrl) {
                    //placeholder(R.drawable.ic_icon_test)
                }
                binding.ivQuestion.isVisible = true
            }
            val testAnswerAdapter = TestAnswerAdapter(item.multipleAnswersAvailable,resources)
            testAnswerAdapter.setOnClickListener {
                Log.d("TAG",it.toString())
            }
            binding.rvAnswers.layoutManager =
                LinearLayoutManager(itemView.context, LinearLayoutManager.VERTICAL, false)
            binding.rvAnswers.addItemDecoration(HorizontalItemDecoration(25))
            binding.rvAnswers.adapter = testAnswerAdapter
            testAnswerAdapter.answers = item.answers
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewPagerTestViewHolder(
            ItemTestBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: ViewPagerTestViewHolder, position: Int) {
        holder.bind(tasks[position],position)
    }

    override fun getItemCount() = tasks.size
}