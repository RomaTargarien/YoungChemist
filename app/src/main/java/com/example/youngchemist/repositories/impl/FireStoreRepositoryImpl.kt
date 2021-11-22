package com.example.youngchemist.repositories.impl

import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.lifecycle.LiveData
import com.example.youngchemist.model.*
import com.example.youngchemist.repositories.FireStoreRepository
import com.example.youngchemist.ui.util.ResourceNetwork
import com.example.youngchemist.ui.util.safeCall
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.lang.Exception
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

    override suspend fun getAllLectures(subjectTitle: String) = withContext(Dispatchers.IO) {
        safeCall {
            val Lectures = firestore.collection(subjectTitle).get().await()
            val LecturesList = mutableListOf<Lecture>()
            Lectures.onEach {
                val pagesList = (it["data"] as ArrayList<String>).map {
                    Page(it)
                }
                LecturesList.add(Lecture(it.id,subjectTitle,pagesList))
            }
            ResourceNetwork.Success(LecturesList.toList())
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

    override suspend fun retriveTest(uid: String) = withContext(Dispatchers.IO) {
        safeCall {
            val result = firestore.collection("tests").document(uid).get().await()
            val test: Test? = result.toObject(Test::class.java)
            ResourceNetwork.Success(test)
        }
    }

}