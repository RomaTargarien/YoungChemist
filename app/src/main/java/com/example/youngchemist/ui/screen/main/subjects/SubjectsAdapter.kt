package com.example.youngchemist.ui.screen.main.subjects

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.example.youngchemist.databinding.ItemSubjectBinding
import com.example.youngchemist.model.Subject

class SubjectsAdapter(val list: List<Subject>): RecyclerView.Adapter<SubjectsAdapter.SubjectViewHolder>() {

    private var onClick: ((String) -> Unit)? = null

    inner class SubjectViewHolder(val binding: ItemSubjectBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Subject) {
            binding.title.setText(item.title)
            binding.ivSubject.setImageResource(item.iconId)
            binding.ivSubject.setOnClickListener {
                onClick?.let { click ->
                    click(item.title)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectViewHolder {
       return SubjectViewHolder(ItemSubjectBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: SubjectViewHolder, position: Int) {
        holder.bind(list[position])
    }
    fun setOnClickListener(listener: (String) -> Unit) {
        onClick = listener
    }

    override fun getItemCount() = list.size
}