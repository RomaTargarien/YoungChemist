package com.example.youngchemist.repositories.impl

import android.util.Log
import com.example.youngchemist.model.*
import com.example.youngchemist.model.user.Model3D
import com.example.youngchemist.model.user.PassedUserTest
import com.example.youngchemist.model.user.UserAchievement
import com.example.youngchemist.model.user.UserProgress
import com.example.youngchemist.repositories.FireStoreRepository
import com.example.youngchemist.ui.util.ResourceNetwork
import com.example.youngchemist.ui.util.safeCall
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class FireStoreRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
) : FireStoreRepository {

    private val subjects = firestore.collection("subjects")
    private val testref = firestore.collection("tests")
    private val achref = firestore.collection("achievements")

    override suspend fun getAllSubjects() = withContext(Dispatchers.IO) {
        safeCall {
            val result = subjects.get().await()
            val subjectsList = mutableListOf<Subject>()
            for (document in result.documents) {
                document.toObject(Subject::class.java)?.let {
                    subjectsList.add(it)
                }
            }
            ResourceNetwork.Success(subjectsList.toList())
        }
    }

    override suspend fun getAllLectures(collectionId: String) = withContext(Dispatchers.IO) {
        safeCall {
            val lectures = firestore.collection(collectionId).get().await()
            val lecturesList = mutableListOf<Lecture>()
            lectures.onEach { document ->
                document.toObject(Lecture::class.java).let {
                    lecturesList.add(it)
                }
            }
            ResourceNetwork.Success(lecturesList.toList())
        }
    }

    override suspend fun get3DModel(modelId: String) = withContext(Dispatchers.IO) {
        safeCall {
            val result = firestore.collection("3Dmodels").document(modelId).get().await()
            val model = result.toObject(Model3D::class.java)
            ResourceNetwork.Success(model)
        }
    }

    override suspend fun saveTest(test: Test) {
        withContext(Dispatchers.IO) {
            try {
                testref.add(test).await()
            } catch (e: Exception) {
                Log.d("Tag", e.localizedMessage)
            }
        }
    }

    override suspend fun saveTest(
        userUid: String,
        passedUserTest: PassedUserTest
    ): ResourceNetwork<String> {
        return withContext(Dispatchers.IO) {
            safeCall {
                val result = firestore.collection("users").document(userUid).get().await()
                val user = result.toObject(User::class.java)
                Log.d("TAG", user.toString())
                user?.passedUserTests?.add(passedUserTest)
                firestore.collection("users").document(userUid).set(
                    user!!,
                    SetOptions.merge()
                )
                ResourceNetwork.Success("")
            }
        }
    }

    override suspend fun retriveTest(uid: String) = withContext(Dispatchers.IO) {
        safeCall {
            val result = firestore.collection("tests").document(uid).get().await()
            val test: Test? = result.toObject(Test::class.java)
            ResourceNetwork.Success(test)
        }
    }

    override suspend fun getUser(userUid: String) = CoroutineScope(Dispatchers.IO).async {
        safeCall {
            val result = firestore.collection("users").document(userUid).get().await()
            val user = result.toObject(User::class.java)
            ResourceNetwork.Success(user)
        }
    }

    override suspend fun saveLecture(lecture: Lecture) = withContext(Dispatchers.IO) {
        safeCall {
            firestore.collection("titration").document(lecture.lectureId).set(lecture).await()
            ResourceNetwork.Success("")
        }
    }

    override suspend fun save3DModels(userId: String, models3D: List<Model3D>) =
        withContext(Dispatchers.IO) {
            safeCall {
                val result = firestore.collection("users").document(userId).get().await()
                val user = result.toObject(User::class.java)
                user?.saved3DModels = ArrayList(models3D)
                firestore.collection("users").document(userId).set(
                    user!!,
                    SetOptions.merge()
                )
                ResourceNetwork.Success("")
            }
        }

    override suspend fun saveUserProgress(
        userId: String,
        userProgress: List<UserProgress>
    ) = withContext(Dispatchers.IO) {
        safeCall {
            val result = firestore.collection("users").document(userId).get().await()
            val user = result.toObject(User::class.java)
            user?.userProgress = ArrayList(userProgress)
            firestore.collection("users").document(userId).set(
                user!!,
                SetOptions.merge()
            )
            ResourceNetwork.Success("")
        }
    }

    override suspend fun saveUserDoneAchievements(
        userId: String,
        doneAchievements: List<UserAchievement>
    ) = withContext(Dispatchers.IO) {
        safeCall {
            val result = firestore.collection("users").document(userId).get().await()
            val user = result.toObject(User::class.java)
            user?.doneAchievements = ArrayList(doneAchievements)
            firestore.collection("users").document(userId).set(
                user!!,
                SetOptions.merge()
            )
            ResourceNetwork.Success("")
        }
    }

    override suspend fun getUserModels3D(userId: String) = withContext(Dispatchers.IO) {
        safeCall {
            val result = firestore.collection("users").document(userId).get().await()
            val user = result.toObject(User::class.java)
            ResourceNetwork.Success(user?.saved3DModels)
        }
    }

    override suspend fun updateUserInfo(user: User) = withContext(Dispatchers.IO) {
        safeCall {
            firestore.collection("users").document(user.uid).set(user, SetOptions.merge())
            ResourceNetwork.Success("")
        }
    }

    override suspend fun saveAchivement(achievement: Achievement) = withContext(Dispatchers.IO) {
        safeCall {
            achref.document(achievement.id).set(achievement)
            ResourceNetwork.Success("")
        }
    }

    override suspend fun getAllAchivements() = withContext(Dispatchers.IO) {
        safeCall {
            val result = achref.get().await()
            val achievements = mutableListOf<Achievement>()
            result.documents.forEach { snapshot ->
                snapshot.toObject(Achievement::class.java)?.let {
                    achievements.add(it)
                }
            }
            ResourceNetwork.Success(achievements.toList())
        }
    }
}