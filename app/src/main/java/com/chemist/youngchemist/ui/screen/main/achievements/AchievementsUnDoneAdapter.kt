package com.chemist.youngchemist.ui.screen.main.achievements

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.chemist.youngchemist.databinding.ItemAchievementBinding
import com.chemist.youngchemist.model.user.UserAchievement
import com.squareup.picasso.Picasso

class AchievementsUnDoneAdapter : RecyclerView.Adapter<AchievementsUnDoneAdapter.AchievementsViewHolder>() {

    private val achievements: MutableList<UserAchievement> = mutableListOf()

    fun submitList(modelsList: List<UserAchievement>) {
        val result: DiffUtil.DiffResult =
            DiffUtil.calculateDiff(AchievementsDiffCallback(achievements, modelsList))
        result.dispatchUpdatesTo(this)
        achievements.clear()
        achievements.addAll(modelsList)
    }

    inner class AchievementsViewHolder(private val binding: ItemAchievementBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: UserAchievement) {
            item.apply {
                Picasso.get().load(imageUrl).into(binding.ivAchievement)
                binding.tvTitle.text = title
                binding.tvAchievementProgress.text = "$itemsDone/$itemsToDone"
                binding.pbAchievement.progress = evaluateProgress(itemsDone,itemsToDone)
            }
        }
    }

    private fun evaluateProgress(itemsDone: Int,itemsToDone: Int): Int {
        val progress = itemsDone.toDouble()/itemsToDone.toDouble()
        return (progress*100).toInt()
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