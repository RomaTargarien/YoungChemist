package com.chemist.youngchemist.repositories.impl

import com.chemist.youngchemist.model.*
import com.chemist.youngchemist.model.user.Model3D
import com.chemist.youngchemist.model.user.PassedUserTest
import com.chemist.youngchemist.model.user.UserAchievement
import com.chemist.youngchemist.model.user.UserProgress
import com.chemist.youngchemist.repositories.FireStoreRepository
import com.chemist.youngchemist.ui.util.constants.Constants.EMPTY_SUCCESS
import com.chemist.youngchemist.ui.util.ResourceNetwork
import com.chemist.youngchemist.ui.util.constants.Collection
import com.chemist.youngchemist.ui.util.safeCall
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FireStoreRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : FireStoreRepository {

    private val subjects = firestore.collection(Collection.SUBJECTS)
    private val achievements = firestore.collection(Collection.ACHIEVEMENTS)
    private val users = firestore.collection(Collection.USERS)
    private val models3D = firestore.collection(Collection.MODELS3D)

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
            val result = models3D.document(modelId).get().await()
            val model = result.toObject(Model3D::class.java)
            ResourceNetwork.Success(model)
        }
    }

    override suspend fun saveTest(
        userUid: String,
        passedUserTest: PassedUserTest
    ): ResourceNetwork<String> {
        return withContext(Dispatchers.IO) {
            safeCall {
                val result = users.document(userUid).get().await()
                val user = result.toObject(User::class.java)
                user?.passedUserTests?.add(passedUserTest)
                users.document(userUid).set(
                    user!!,
                    SetOptions.merge()
                )
                ResourceNetwork.Success(EMPTY_SUCCESS)
            }
        }
    }

    override suspend fun getUser(userUid: String) = CoroutineScope(Dispatchers.IO).async {
        safeCall {
            val result = users.document(userUid).get().await()
            val user = result.toObject(User::class.java)
            ResourceNetwork.Success(user)
        }
    }

    override suspend fun save3DModels(userId: String, models3D: List<Model3D>) =
        withContext(Dispatchers.IO) {
            safeCall {
                val result = users.document(userId).get().await()
                val user = result.toObject(User::class.java)
                user?.saved3DModels = ArrayList(models3D)
                users.document(userId).set(
                    user!!,
                    SetOptions.merge()
                )
                ResourceNetwork.Success(EMPTY_SUCCESS)
            }
        }

    override suspend fun saveUserProgress(
        userId: String,
        userProgress: List<UserProgress>
    ) = withContext(Dispatchers.IO) {
        safeCall {
            val result = users.document(userId).get().await()
            val user = result.toObject(User::class.java)
            user?.userProgress = ArrayList(userProgress)
            users.document(userId).set(
                user!!,
                SetOptions.merge()
            )
            ResourceNetwork.Success(EMPTY_SUCCESS)
        }
    }

    override suspend fun saveUserDoneAchievements(
        userId: String,
        doneAchievements: List<UserAchievement>
    ) = withContext(Dispatchers.IO) {
        safeCall {
            val result = users.document(userId).get().await()
            val user = result.toObject(User::class.java)
            user?.doneAchievements = ArrayList(doneAchievements)
            users.document(userId).set(
                user!!,
                SetOptions.merge()
            )
            ResourceNetwork.Success(EMPTY_SUCCESS)
        }
    }

    override suspend fun getUserModels3D(userId: String) = withContext(Dispatchers.IO) {
        safeCall {
            val result = users.document(userId).get().await()
            val user = result.toObject(User::class.java)
            ResourceNetwork.Success(user?.saved3DModels)
        }
    }

    override suspend fun updateUserInfo(user: User) = withContext(Dispatchers.IO) {
        safeCall {
            users.document(user.uid).set(user, SetOptions.merge())
            ResourceNetwork.Success(EMPTY_SUCCESS)
        }
    }

    override suspend fun saveAchivement(achievement: Achievement) = withContext(Dispatchers.IO) {
        safeCall {
            achievements.document(achievement.id).set(achievement)
            ResourceNetwork.Success(EMPTY_SUCCESS)
        }
    }

    override suspend fun getAllAchivements() = withContext(Dispatchers.IO) {
        safeCall {
            val result = achievements.get().await()
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