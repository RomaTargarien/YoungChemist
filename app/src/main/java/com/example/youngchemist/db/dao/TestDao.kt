package com.example.youngchemist.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.youngchemist.model.user.PassedUserTest

@Dao
interface TestDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun savePassedUserTest(passedUserTest: PassedUserTest)

    @Query("SELECT * FROM unsavedTests WHERE userUid LIKE :userId")
    fun getAllTests(userId: String): List<PassedUserTest>
}