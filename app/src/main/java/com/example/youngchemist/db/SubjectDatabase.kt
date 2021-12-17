package com.example.youngchemist.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.youngchemist.db.dao.*
import com.example.youngchemist.model.Lecture
import com.example.youngchemist.model.Subject
import com.example.youngchemist.model.user.Model3D
import com.example.youngchemist.model.user.PassedUserTest
import com.example.youngchemist.model.user.UserProgress

@Database(
    entities = [Subject::class, Lecture::class, PassedUserTest::class, Model3D::class, UserProgress::class],
    version = 18
)
@TypeConverters(Converters::class)
abstract class SubjectDatabase : RoomDatabase() {
    abstract fun getSubjectdao(): SubjectDao
    abstract fun getLectureDao(): LectureDao
    abstract fun getTestDao(): TestDao
    abstract fun getModel3DDao(): Model3DDao
    abstract fun getUserProgressDao(): UserProgressDao
}