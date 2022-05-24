package com.chemist.youngchemist.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.chemist.youngchemist.db.dao.*
import com.chemist.youngchemist.model.Lecture
import com.chemist.youngchemist.model.Subject
import com.chemist.youngchemist.model.user.Model3D
import com.chemist.youngchemist.model.user.PassedUserTest
import com.chemist.youngchemist.model.user.UserAchievement
import com.chemist.youngchemist.model.user.UserProgress

@Database(
    entities = [
        Subject::class,
        Lecture::class,
        PassedUserTest::class,
        Model3D::class,
        UserProgress::class,
        UserAchievement::class],
    version = 27
)
@TypeConverters(Converters::class)
abstract class SubjectDatabase : RoomDatabase() {
    abstract fun getSubjectDao(): SubjectDao
    abstract fun getLectureDao(): LectureDao
    abstract fun getTestDao(): TestDao
    abstract fun getModel3DDao(): Model3DDao
    abstract fun getUserProgressDao(): UserProgressDao
    abstract fun getAchievementsDao(): AchievementsDao
}