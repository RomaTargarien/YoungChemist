package com.example.youngchemist.ui.screen.main.subjects.lectures.lectures_list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.youngchemist.databinding.ItemLectureInListBinding
import com.example.youngchemist.databinding.ItemSubjectBinding
import com.example.youngchemist.model.Subject
import com.example.youngchemist.ui.screen.main.subjects.SubjectsAdapter

class LecturesListAdapter(val list: List<String>): RecyclerView.Adapter<LecturesListAdapter.LectureViewHolder>() {

    class LectureViewHolder(val binding: ItemLectureInListBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: String) {
            binding.tvTitle.setText(item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LectureViewHolder {
        return LectureViewHolder(ItemLectureInListBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: LectureViewHolder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount() = list.size
}