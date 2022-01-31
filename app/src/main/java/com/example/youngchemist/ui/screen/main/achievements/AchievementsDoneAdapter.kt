package com.example.youngchemist.ui.screen.main.achievements

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.youngchemist.R
import com.example.youngchemist.databinding.ItemDoneAchievementBinding
import com.example.youngchemist.model.user.UserAchievement
import com.mikhaellopez.circularprogressbar.CircularProgressBar
import com.squareup.picasso.Picasso

class AchievementsDoneAdapter() :
    RecyclerView.Adapter<AchievementsDoneAdapter.AchievementsViewHolder>() {

    private val achievements: MutableList<UserAchievement> = mutableListOf()

    private val mapBinding: MutableMap<Int, ItemDoneAchievementBinding> = mutableMapOf()
    private var previousSelectedPosition: Int? = null

    private var onClick: ((Pair<String, Boolean>) -> Unit)? = null
    fun setOnClickListener(listener: (Pair<String, Boolean>) -> Unit) {
        onClick = listener
    }

    fun submitList(modelsList: List<UserAchievement>) {
        val result: DiffUtil.DiffResult =
            DiffUtil.calculateDiff(AchievementsDiffCallback(achievements, modelsList))
        result.dispatchUpdatesTo(this)
        achievements.clear()
        achievements.addAll(modelsList)
    }

    inner class AchievementsViewHolder(val binding: ItemDoneAchievementBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: UserAchievement, position: Int) {
            item.apply {
                Picasso.get().load(imageUrl).placeholder(R.drawable.ic_icon_happy_flask)
                    .into(binding.ivDoneAchievement)
            }
            binding.ivDoneAchievement.setOnClickListener {
                clickListener(position, item)
            }
        }

        private fun clickListener(position: Int, item: UserAchievement) {
            if (position != previousSelectedPosition && previousSelectedPosition != null) {
                achievementTitleBehavior(item.title, true)
                mapBinding[previousSelectedPosition]?.pbSelection?.apply {
                    setProgressWithAnimation(0f, 500)
                    progressDirection = CircularProgressBar.ProgressDirection.TO_RIGHT
                }
                previousSelectedPosition = null
            }
            if (position == previousSelectedPosition) {
                achievementTitleBehavior(item.title, false)
                binding.pbSelection.apply {
                    setProgressWithAnimation(0f, 500)
                    progressDirection = CircularProgressBar.ProgressDirection.TO_RIGHT
                }
                previousSelectedPosition = null
            } else {
                binding.pbSelection.apply {
                    setProgressWithAnimation(100f, 500)
                    progressDirection = CircularProgressBar.ProgressDirection.TO_LEFT
                }
                achievementTitleBehavior(item.title, true)
                previousSelectedPosition = position
            }
        }


        private fun achievementTitleBehavior(title: String, isVisible: Boolean) {
            onClick?.let { click ->
                click(Pair(title, isVisible))
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
        mapBinding[position] = holder.binding
        mapBinding.filterValues { it == holder.binding }.keys
            .toList()
            .filter { it != position }
            .forEach {
                mapBinding.remove(it)
            }
        holder.bind(achievements[position], position)
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