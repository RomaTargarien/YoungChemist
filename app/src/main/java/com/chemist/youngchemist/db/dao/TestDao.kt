package com.chemist.youngchemist.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.chemist.youngchemist.model.user.PassedUserTest
import kotlinx.coroutines.flow.Flow

@Dao
interface TestDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun savePassedUserTest(passedUserTest: PassedUserTest)

    @Query("SELECT * FROM unsavedTests WHERE userUid LIKE :userId")
    fun getAllTests(userId: String): Flow<List<PassedUserTest>>

    @Query("SELECT * FROM unsavedTests WHERE userUid LIKE :userId AND testUid LIKE :testId")
    fun getTest(userId: String,testId: String): List<PassedUserTest>
}