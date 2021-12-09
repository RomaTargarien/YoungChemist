package com.example.youngchemist.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.youngchemist.model.Lecture

@Dao
interface LectureDao {

    @Query("SELECT * FROM lectures WHERE collectionId LIKE :collectionId")
    fun getAllLectures(collectionId: String): List<Lecture>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNewLectures(lectures: List<Lecture>)

//    @Query("SELECT * FROM lectures WHERE (title LIKE :lectureTitle) AND (subjectTitle LIKE :subjectTitle)")
//    fun getAllPages(lectureTitle: String,subjectTitle: String): List<Lecture>



}