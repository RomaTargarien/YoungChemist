package com.example.youngchemist.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.example.youngchemist.model.PassedUserTest

@Dao
interface TestDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun savePassedUserTest(passedUserTest: PassedUserTest)
}