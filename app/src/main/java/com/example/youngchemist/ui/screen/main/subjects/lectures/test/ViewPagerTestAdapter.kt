package com.example.youngchemist.ui.screen.main.subjects.lectures.test

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.youngchemist.databinding.ItemTestBinding
import com.example.youngchemist.model.Task
import com.example.youngchemist.model.TaskUi

class ViewPagerTestAdapter(
    private val resources: Resources
) : RecyclerView.Adapter<ViewPagerTestAdapter.ViewPagerTestViewHolder>() {

    private val differCallBack = object : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }
    }
    private val userTaskList: MutableList<TaskUi> = mutableListOf()

    private var onClick: ((MutableList<TaskUi>) -> Unit)? = null
    fun setOnClickListener(listener: (MutableList<TaskUi>) -> Unit) {
        onClick = listener
    }

    private val differ = AsyncListDiffer(this, differCallBack)

    var tasks: List<Task>
        get() = differ.currentList
        set(value) = differ.submitList(value).also {
            for (i in 0..value.size-1) {
                userTaskList.add(TaskUi(i, emptyList()))
            }
        }

    inner class ViewPagerTestViewHolder(val binding: ItemTestBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Task,position: Int) {
            binding.tvQuestion.text = "${position+1}) ${item.question}"
            if (item.imageToQuestionUrl.isNotEmpty()) {
                binding.ivQuestion.load(item.imageToQuestionUrl)
                binding.ivQuestion.isVisible = true
            }
            initRecyclerView(binding,item,itemView.context,position)
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

    private fun initRecyclerView(binding: ItemTestBinding,task: Task,context: Context,position: Int) {
        val testAnswerAdapter = TestAnswerAdapter(task.multipleAnswersAvailable,resources)
        testAnswerAdapter.setOnClickListener {
            onClick?.let { click ->
                userTaskList[position].answersList = it
                click(userTaskList)
            }
        }
        binding.rvAnswers.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        binding.rvAnswers.addItemDecoration(HorizontalItemDecoration(25))
        binding.rvAnswers.adapter = testAnswerAdapter
        testAnswerAdapter.answers = task.answers
    }
}