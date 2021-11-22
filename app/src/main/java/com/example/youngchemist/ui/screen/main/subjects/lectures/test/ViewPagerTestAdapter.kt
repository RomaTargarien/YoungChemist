package com.example.youngchemist.ui.screen.main.subjects.lectures.test

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.youngchemist.databinding.ItemTestBinding
import com.example.youngchemist.model.Subject

class ViewPagerTestAdapter: RecyclerView.Adapter<ViewPagerTestAdapter.ViewPagerTestViewHolder>()  {

    private val differCallBack = object : DiffUtil.ItemCallback<Map<String,ArrayList<Map<String,Boolean>>>>() {
        override fun areItemsTheSame(oldItem: Map<String,ArrayList<Map<String,Boolean>>>, newItem: Map<String,ArrayList<Map<String,Boolean>>>): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

        override fun areContentsTheSame(oldItem: Map<String,ArrayList<Map<String,Boolean>>>, newItem: Map<String,ArrayList<Map<String,Boolean>>>): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

    }

    private val differ = AsyncListDiffer(this,differCallBack)

    var questions: MutableList<Map<String, ArrayList<Map<String, Boolean>>>>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    inner class ViewPagerTestViewHolder(val binding: ItemTestBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Map<String, ArrayList<Map<String, Boolean>>>) {
           // binding.tvQuestion.text = item.
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewPagerTestViewHolder(ItemTestBinding.inflate(LayoutInflater.from(parent.context),parent,false))

    override fun onBindViewHolder(holder: ViewPagerTestViewHolder, position: Int) {

    }

    override fun getItemCount() = questions.size
}