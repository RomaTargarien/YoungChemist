package com.example.youngchemist.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.youngchemist.model.user.UserAchievement
import kotlinx.coroutines.flow.Flow

@Dao
interface AchievementsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAchievement(userAchievement: UserAchievement)

    @Query("SELECT * FROM achievements WHERE userId LIKE :userId")
    fun getAchievements(userId: String): Flow<List<UserAchievement>>
}