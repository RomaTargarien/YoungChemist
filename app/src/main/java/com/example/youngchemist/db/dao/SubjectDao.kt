package com.example.youngchemist.db.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.youngchemist.model.Subject

@Dao
interface SubjectDao {

    @Query("SELECT * FROM subjects")
    fun getAllSubjects(): List<Subject>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNewSubjects(subjects: List<Subject>)

    @Query("DELETE FROM subjects")
    suspend fun delete()

}