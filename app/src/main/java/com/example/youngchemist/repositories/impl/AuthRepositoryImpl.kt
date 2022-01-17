package com.example.youngchemist.repositories.impl

import com.example.youngchemist.model.AuthResults
import com.example.youngchemist.model.User
import com.example.youngchemist.repositories.AuthRepository
import com.example.youngchemist.ui.util.ResourceNetwork
import com.example.youngchemist.ui.util.safeCall
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {

    val users = firestore.collection("users")

    override suspend fun register(
        authResults: AuthResults
    ) = withContext(Dispatchers.IO) {
        safeCall {
            val result =
                auth.createUserWithEmailAndPassword(authResults.login!!, authResults.password!!)
                    .await()
            val uid = result.user?.uid
            val user = User(uid!!, authResults.name!!, authResults.surname!!)
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

    override suspend fun reauthenticate(password: String) = withContext(Dispatchers.IO) {
        safeCall {
            auth.currentUser?.email?.let {
                val credentials = EmailAuthProvider.getCredential(it, password)
                auth.currentUser?.reauthenticate(credentials)?.await()
            }
            ResourceNetwork.Success("")
        }
    }

    override suspend fun updateEmail(email: String) = withContext(Dispatchers.IO) {
        safeCall {
            auth.currentUser?.updateEmail(email)
            ResourceNetwork.Success("")
        }
    }

    override suspend fun updatePassword(password: String) = withContext(Dispatchers.IO) {
        auth.currentUser?.updatePassword(password)
        ResourceNetwork.Success("")
    }

    override suspend fun logOut() = withContext(Dispatchers.IO) {
        safeCall {
            Firebase.auth.signOut()
            ResourceNetwork.Success("")
        }
    }
}