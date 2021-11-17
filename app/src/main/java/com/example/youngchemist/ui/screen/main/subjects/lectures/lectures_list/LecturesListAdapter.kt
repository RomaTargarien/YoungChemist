package com.example.youngchemist.ui.screen.main.subjects.lectures.lectures_list

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.youngchemist.databinding.ItemLectureInListBinding
import com.example.youngchemist.databinding.ItemSubjectBinding
import com.example.youngchemist.model.Lection
import com.example.youngchemist.model.Subject
import com.example.youngchemist.ui.screen.main.subjects.SubjectsAdapter

class LecturesListAdapter: RecyclerView.Adapter<LecturesListAdapter.LectureViewHolder>() {

    private val differCallBack = object : DiffUtil.ItemCallback<Lection>() {
        override fun areItemsTheSame(oldItem: Lection, newItem: Lection): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

        override fun areContentsTheSame(oldItem: Lection, newItem: Lection): Boolean {
            return oldItem.title == newItem.title
        }

    }

    private val differ = AsyncListDiffer(this,differCallBack)

    var lections: List<Lection>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    inner class LectureViewHolder(val binding: ItemLectureInListBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Lection) {
            binding.tvTitle.setText(item.title)
            binding.tvTitle.setOnClickListener {
                onClick?.let { click ->
                    click(item.title)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LectureViewHolder {
        return LectureViewHolder(ItemLectureInListBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: LectureViewHolder, position: Int) {
        holder.bind(lections[position])
    }

    override fun getItemCount() = lections.size

    private var onClick: ((String) -> Unit)? = null
    fun setOnClickListener(listener: (String) -> Unit) {
        onClick = listener
    }
}