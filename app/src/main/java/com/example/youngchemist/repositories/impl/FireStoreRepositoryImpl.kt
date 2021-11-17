package com.example.youngchemist.repositories.impl

import android.util.Log
import androidx.lifecycle.LiveData
import com.example.youngchemist.model.Subject
import com.example.youngchemist.repositories.FireStoreRepository
import com.example.youngchemist.ui.util.ResourceNetwork
import com.example.youngchemist.ui.util.safeCall
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
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
            result.forEach {
                val title = it["title"] as String
                val icon_url = it["icon_url"] as String
                subjectsList.add(Subject(title,icon_url))
            }
            ResourceNetwork.Success(subjectsList.toList())
        }
    }

}