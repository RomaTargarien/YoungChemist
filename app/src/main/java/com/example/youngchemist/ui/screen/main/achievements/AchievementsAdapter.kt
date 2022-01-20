package com.example.youngchemist.ui.screen.main.achievements

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.youngchemist.databinding.ItemAchievementBinding
import com.example.youngchemist.model.Achievement
import com.squareup.picasso.Picasso

class AchievementsAdapter : RecyclerView.Adapter<AchievementsAdapter.AchievementsViewHolder>() {

    private val achievements: MutableList<Achievement> = mutableListOf()

    fun submitList(modelsList: List<Achievement>) {
        val result: DiffUtil.DiffResult =
            DiffUtil.calculateDiff(AchievementsDiffCallback(achievements, modelsList))
        result.dispatchUpdatesTo(this)
        achievements.clear()
        achievements.addAll(modelsList)
    }

    inner class AchievementsViewHolder(private val binding: ItemAchievementBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Achievement) {
            Picasso.get().load(item.imageUrl).into(binding.ibLocation)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AchievementsViewHolder {
        return AchievementsViewHolder(
            ItemAchievementBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: AchievementsViewHolder, position: Int) {
        holder.bind(achievements[position])
    }

    override fun getItemCount(): Int = achievements.size

    inner class AchievementsDiffCallback(
        private val oldList: List<Achievement>,
        private val newList: List<Achievement>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int {
            return oldList.size
        }

        override fun getNewListSize(): Int {
            return newList.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].hashCode() == newList[newItemPosition].hashCode()
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldList[oldItemPosition].id == newList[newItemPosition].id
        }
    }
}