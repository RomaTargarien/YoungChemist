package com.example.youngchemist.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.youngchemist.model.Subject
import kotlinx.coroutines.flow.Flow

@Dao
interface SubjectDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSubject(subject: Subject)

    @Query("SELECT * FROM subjects")
    fun getSubjects(): Flow<List<Subject>>

    @Query("SELECT * FROM subjects WHERE subjectPrimaryKey LIKE :primaryKey")
    fun getSubjectByPrimaryKey(primaryKey: Int): Subject

}