package com.example.youngchemist.model.user

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.youngchemist.model.Achievement
import com.example.youngchemist.model.UserInfo

@Entity(tableName = "achievements")
data class UserAchievement(
    val id: String = "",
    var userId: String = "",
    val title: String = "",
    var imageUrl: String = "",
    var itemsDone: Int = 0,
    var itemsToDone: Int = 0,
    var wasViewed: Boolean = false
) : UserInfo {
    @PrimaryKey(autoGenerate = true)
    var achievementPrimaryKey: Int = 0

    fun testsAchievementProgress(
        achievement: Achievement,
        doneTests: List<PassedUserTest>
    ) {
        achievement.testsToDone?.let { testsToDone ->
            itemsDone = if (testsToDone >= doneTests.size) doneTests.size else testsToDone
            achievement.testsMark?.let { mark ->
                testAchievemntWithMarkProgress(doneTests, mark, testsToDone)
            }
            achievement.testsAverageMark?.let { avarageMark ->
                testAchievemntWithAverageMarkProgress(doneTests, avarageMark, testsToDone)
            }
        }
    }

    fun registrationAchievementProgress() {
        itemsDone = 1
    }

    fun modelsAchievementProgress(
        achievement: Achievement,
        savedModels: List<Model3D>
    ) {
        itemsDone =
            if (achievement.modelsToSave!! >= savedModels.size) savedModels.size else achievement.modelsToSave
    }

    fun readenLecturesAchievementProgress(
        achievement: Achievement,
        readenLectures: List<UserProgress>
    ) {
        readenLectures.filter {
            it.isLectureReaden
        }.also {
            itemsDone =
                if (achievement.lecturesToRead!! >= it.size) it.size else achievement.lecturesToRead
        }
    }

    private fun testAchievemntWithMarkProgress(
        doneTests: List<PassedUserTest>,
        mark: Int,
        testsToDone: Int
    ) {
        doneTests.filter {
            it.mark >= mark.toDouble()
        }.also {
            itemsDone = if (doneTests.size >= it.size) it.size else testsToDone
        }
    }

    private fun testAchievemntWithAverageMarkProgress(
        doneTests: List<PassedUserTest>,
        avarageMark: Int,
        testsToDone: Int
    ) {
        if (testsToDone <= doneTests.size) {
            doneTests.sumOf {
                it.mark
            }.also {
                val currentAvarageMark = it / doneTests.size
                if (currentAvarageMark >= avarageMark) {
                    itemsDone = if (testsToDone >= doneTests.size) doneTests.size else testsToDone
                } else {
                    itemsDone = 0
                }
            }
        } else {
            itemsDone = 0
        }
    }

}
