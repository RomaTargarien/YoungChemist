package com.example.youngchemist.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.youngchemist.model.user.UserProgress

@Dao
interface UserProgressDao {

    @Query("SELECT * FROM userProgress WHERE userId LIKE :userId")
    fun getProgress(userId: String): List<UserProgress>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun writeProgress(userProgress: UserProgress)

}