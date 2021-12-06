package com.example.youngchemist.repositories.impl

import android.content.Intent
import android.net.Uri
import android.system.ErrnoException
import android.util.Log
import androidx.lifecycle.LiveData
import com.example.youngchemist.model.*
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
import java.lang.Exception
import java.net.UnknownHostException
import java.util.ArrayList
import javax.inject.Inject

class FireStoreRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage
): FireStoreRepository {

    private val subjects = firestore.collection("subjects")
    private val testref = firestore.collection("tests")

    override suspend fun getAllSubjects() = withContext(Dispatchers.IO) {
        safeCall {
            val result = subjects.get().await()
            Log.d("TAG",result.toString())
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

    override suspend fun get3DModel(name: String) = withContext(Dispatchers.IO) {
        safeCall {
            val model = storage.getReference(name)
            val uri = model.downloadUrl.await()
            val intentUri = Uri.parse("https://arvr.google.com/scene-viewer/1.0")
                .buildUpon()
                .appendQueryParameter(
                    "file",
                    uri.toString()
                )
                .appendQueryParameter("mode","3d_only")
                .build()
            ResourceNetwork.Success(intentUri)
        }
    }

    override suspend fun saveTest(test: Test) {
        withContext(Dispatchers.IO) {
            try {
                Log.d("TAG","save")
                testref.add(test).await()
            } catch (e: Exception) {
                Log.d("Tag",e.localizedMessage)
            }
        }
    }

    override suspend fun saveTest(userUid: String,passedUserTest: PassedUserTest): ResourceNetwork<String> {
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

    override suspend fun updateReadenLectures(lectureId: String) = withContext(Dispatchers.IO) {
        safeCall {
            val result = firestore.collection("users").document("dJuRGOc06xhllmscaAEqQoHC9Ir2").get().await()
            val user = result.toObject(User::class.java)
            user?.readenLectures?.add(lectureId)
            firestore.collection("users").document("dJuRGOc06xhllmscaAEqQoHC9Ir2").set(
                user!!,
                SetOptions.merge()
            )
            ResourceNetwork.Success("")
        }
    }
}