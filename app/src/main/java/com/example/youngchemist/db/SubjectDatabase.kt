package com.example.youngchemist.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.youngchemist.db.dao.LectureDao
import com.example.youngchemist.db.dao.Model3DDao
import com.example.youngchemist.db.dao.SubjectDao
import com.example.youngchemist.db.dao.TestDao
import com.example.youngchemist.model.Lecture
import com.example.youngchemist.model.Subject
import com.example.youngchemist.model.user.Model3D
import com.example.youngchemist.model.user.PassedUserTest

@Database(entities = [Subject::class, Lecture::class, PassedUserTest::class,Model3D::class], version = 13)
@TypeConverters(Converters::class)
abstract class SubjectDatabase : RoomDatabase() {
    abstract fun getSubjectdao(): SubjectDao
    abstract fun getLectureDao(): LectureDao
    abstract fun getTestDao(): TestDao
    abstract fun getModel3DDao(): Model3DDao
}