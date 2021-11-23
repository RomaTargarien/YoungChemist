package com.example.youngchemist.ui.screen.main.subjects.lectures.lecture

import android.annotation.SuppressLint
import android.content.res.Resources
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.youngchemist.R
import com.example.youngchemist.databinding.ItemPageNumberBinding
import com.example.youngchemist.model.Page
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filterNotNull
import androidx.core.view.ViewCompat




class PagesPaginationAdapter(
    private val resources: Resources,
    private val viewModel: LectureFragmentViewModel,
    private val lifecycle: LifecycleCoroutineScope
) :
    RecyclerView.Adapter<PagesPaginationAdapter.PagePaginationViewHolder>() {

    private val differCallBack = object : DiffUtil.ItemCallback<Page>() {
        override fun areItemsTheSame(oldItem: Page, newItem: Page): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

        override fun areContentsTheSame(oldItem: Page, newItem: Page): Boolean {
            return oldItem.data == newItem.data
        }
    }

    private var previousPageNumber: Int = 0
    private val mapBinding: MutableMap<Int, ItemPageNumberBinding> = mutableMapOf()

    private val differ = AsyncListDiffer(this, differCallBack)

    var pages: List<Page>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    init {
        lifecycle.launch {
            viewModel.selectedPage.filterNotNull().collect {
                Log.d("TAG",it.toString())
                mapBinding[previousPageNumber]?.pageNumberContainer?.background = resources.getDrawable(R.drawable.shape_outlined_rectangle)
                mapBinding[it]?.pageNumberContainer?.background = resources.getDrawable(R.drawable.shape_rounded_button)
                previousPageNumber = it
            }
        }
    }

    inner class PagePaginationViewHolder(val binding: ItemPageNumberBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.pageNumberContainer.background = resources.getDrawable(R.drawable.shape_outlined_rectangle)
            mapBinding[previousPageNumber]?.pageNumberContainer?.background = resources.getDrawable(R.drawable.shape_rounded_button)
            binding.tvPageNumber.setText((position + 1).toString())
            binding.pageNumberContainer.setOnClickListener {
                onClick?.let { click ->
                    if (position != previousPageNumber) {
                        binding.pageNumberContainer.background = resources.getDrawable(R.drawable.shape_rounded_button)
                        mapBinding[previousPageNumber]?.pageNumberContainer?.background = resources.getDrawable(R.drawable.shape_outlined_rectangle)
                        previousPageNumber = position
                    }
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
        mapBinding[position] = holder.binding
        mapBinding.filterValues { it == holder.binding }.keys
                .toList()
                .filter { it != position }
                .forEach {
                    mapBinding.remove(it)
                }
        holder.bind(position)
    }

    override fun getItemCount() = pages.size

    private var onClick: ((Int) -> Unit)? = null
    fun setOnClickListener(listener: (Int) -> Unit) {
        onClick = listener
    }

}