package com.example.youngchemist.ui.screen.main.subjects

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.example.youngchemist.databinding.ItemBinding
import com.example.youngchemist.model.Subject

class SubjectsAdapter(val list: List<Subject>): RecyclerView.Adapter<SubjectsAdapter.SubjectViewHolder>() {

    class SubjectViewHolder(val binding: ItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Subject) {
            binding.title.setText(item.title)
            binding.ivSubject.setImageResource(item.iconId)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectViewHolder {
       return SubjectViewHolder(ItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: SubjectViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount() = list.size
}