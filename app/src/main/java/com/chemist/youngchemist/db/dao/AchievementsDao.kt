package com.chemist.youngchemist.db.dao

import androidx.room.*
import com.chemist.youngchemist.model.user.UserAchievement
import kotlinx.coroutines.flow.Flow

@Dao
interface AchievementsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAchievement(userAchievement: UserAchievement)

    @Query("SELECT * FROM achievements WHERE userId LIKE :userId")
    fun getAchievements(userId: String): Flow<List<UserAchievement>>

    @Query("SELECT * FROM achievements WHERE achievementPrimaryKey LIKE :primaryKey")
    fun getAchievementByPrimaryKey(primaryKey: Int): UserAchievement

    @Query("SELECT * FROM achievements")
    fun getAllAchievements(): List<UserAchievement>
}