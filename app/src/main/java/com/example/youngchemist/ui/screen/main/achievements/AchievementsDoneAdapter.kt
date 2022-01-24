package com.example.youngchemist.ui.screen.main.achievements

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.youngchemist.databinding.ItemDoneAchievementBinding
import com.example.youngchemist.model.user.UserAchievement
import com.squareup.picasso.Picasso

class AchievementsDoneAdapter() :
    RecyclerView.Adapter<AchievementsDoneAdapter.AchievementsViewHolder>() {

    private val achievements: MutableList<UserAchievement> = mutableListOf()

    fun submitList(modelsList: List<UserAchievement>) {
        val result: DiffUtil.DiffResult =
            DiffUtil.calculateDiff(AchievementsDiffCallback(achievements, modelsList))
        result.dispatchUpdatesTo(this)
        achievements.clear()
        achievements.addAll(modelsList)
    }

    inner class AchievementsViewHolder(private val binding: ItemDoneAchievementBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: UserAchievement) {
            item.apply {
                Picasso.get().load(imageUrl).into(binding.ivDoneAchievement)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AchievementsViewHolder {
        return AchievementsViewHolder(
            ItemDoneAchievementBinding.inflate(
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
        private val oldList: List<UserAchievement>,
        private val newList: List<UserAchievement>
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