package com.example.youngchemist.ui.screen.main.subjects.lectures.lecture

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.youngchemist.databinding.ItemPageNumberBinding
import com.example.youngchemist.model.Page

class PagesPaginationAdapter :
    RecyclerView.Adapter<PagesPaginationAdapter.PagePaginationViewHolder>() {

    private var items: List<Page> = listOf()

    fun setItems(newItems: List<Page>) {
        items = newItems
        notifyDataSetChanged()
    }

    inner class PagePaginationViewHolder(val binding: ItemPageNumberBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.tvItemPageNumber.text = (position+1).toString()
            binding.listItemPageNumberBackground.setOnClickListener {
                onClick?.let { click ->
                    click(position)
                }
            }
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        PagePaginationViewHolder(
            ItemPageNumberBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )


    override fun onBindViewHolder(holder: PagePaginationViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount() = items.size

    private var onClick: ((Int) -> Unit)? = null
    fun setOnClickListener(listener: (Int) -> Unit) {
        onClick = listener
    }

}