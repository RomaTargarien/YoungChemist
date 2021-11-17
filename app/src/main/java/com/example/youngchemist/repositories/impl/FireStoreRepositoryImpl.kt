package com.example.youngchemist.repositories.impl

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.youngchemist.model.Content
import com.example.youngchemist.model.Lection
import com.example.youngchemist.model.Subject
import com.example.youngchemist.repositories.FireStoreRepository
import com.example.youngchemist.ui.util.ResourceNetwork
import com.example.youngchemist.ui.util.safeCall
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.ArrayList
import javax.inject.Inject

class FireStoreRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
): FireStoreRepository {

    private val subjects = firestore.collection("subjects")

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
            val lections = firestore.collection(subjectTitle).get().await()
            val lectionsList = mutableListOf<Lection>()
            lections.onEach {
                lectionsList.add(Lection(it.id))
            }
            for (doc in lections) {
                Log.d("TAG",doc.id)
            }
            ResourceNetwork.Success(lectionsList.toList())
        }
    }

    override suspend fun getLecture(subjectTitle: String,lectureTitle: String) = withContext(Dispatchers.IO) {
        safeCall {
            val lection = firestore.collection(subjectTitle).document(lectureTitle).get().await()
            val data = lection["data"] as ArrayList<String>
            Log.d("TAG",data[0])
            ResourceNetwork.Success(Content(data))
        }
    }

}