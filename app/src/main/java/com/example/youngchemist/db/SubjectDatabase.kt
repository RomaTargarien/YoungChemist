package com.example.youngchemist.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.youngchemist.model.Lecture
import com.example.youngchemist.model.PassedUserTest
import com.example.youngchemist.model.Subject

@Database(entities = [Subject::class, Lecture::class, PassedUserTest::class], version = 9)
@TypeConverters(Converters::class)
abstract class SubjectDatabase : RoomDatabase() {
    abstract fun getSubjectdao(): SubjectDao
    abstract fun getLectureDao(): LectureDao
    abstract fun getTestDao(): TestDao
}