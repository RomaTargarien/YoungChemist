package com.example.youngchemist.repositories.impl

import android.content.Context
import android.util.Log
import com.example.youngchemist.model.AuthResults
import com.example.youngchemist.model.User
import com.example.youngchemist.repositories.AuthRepository
import com.example.youngchemist.ui.util.ResourceNetwork
import com.example.youngchemist.ui.util.safeCall
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    @ApplicationContext private val context: Context
) : AuthRepository {

    val users = firestore.collection("users")

    override suspend fun register(
        authResults: AuthResults
    ) = withContext(Dispatchers.IO) {
        safeCall {
            Log.d("TAG", "here")
            val result = auth.createUserWithEmailAndPassword(authResults.login!!, authResults.password!!).await()
            Log.d("TAG", result.user.toString())
            val uid = result.user?.uid
            val user = User(uid!!, authResults.surname!!)
            users.document(uid).set(user).await()
            ResourceNetwork.Success("")
        }
    }

    override suspend fun login(login: String, password: String) =
        withContext(Dispatchers.IO) {
            safeCall {
                auth.signInWithEmailAndPassword(login, password).await()
                ResourceNetwork.Success("")
            }
        }

    override suspend fun restorePassword(login: String) =
        withContext(Dispatchers.IO) {
            safeCall {
                auth.sendPasswordResetEmail(login).await()
                ResourceNetwork.Success("")
            }
        }
}