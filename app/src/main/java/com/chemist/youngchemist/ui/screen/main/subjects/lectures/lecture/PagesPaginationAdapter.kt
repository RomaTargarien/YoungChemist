package com.chemist.youngchemist.ui.screen.main.subjects.lectures.lecture

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.chemist.youngchemist.databinding.ItemPageNumberBinding

class PagesPaginationAdapter :
    RecyclerView.Adapter<PagesPaginationAdapter.PagePaginationViewHolder>() {

    private var items: List<String> = listOf()

    fun setItems(newItems: List<String>) {
        items = newItems
        notifyDataSetChanged()
    }

    inner class PagePaginationViewHolder(val binding: ItemPageNumberBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.tvItemPageNumber.text = (position + 1).toString()
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