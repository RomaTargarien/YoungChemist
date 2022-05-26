package com.chemist.youngchemist.repositories.impl

import android.content.res.Resources
import com.chemist.youngchemist.model.User
import com.chemist.youngchemist.repositories.AuthRepository
import com.chemist.youngchemist.ui.util.constants.Constants.EMPTY_SUCCESS
import com.chemist.youngchemist.ui.util.ResourceNetwork
import com.chemist.youngchemist.ui.util.constants.Collection
import com.chemist.youngchemist.ui.util.safeAuthCall
import com.chemist.youngchemist.ui.util.safeCall
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
    private val firestore: FirebaseFirestore,
    private var resources: Resources
) : AuthRepository {

    private val users = firestore.collection(Collection.USERS)

    override suspend fun register(
        login: String,password: String,name: String,surname: String
    ) = withContext(Dispatchers.IO) {
        safeAuthCall(resources) {
            val result = auth.createUserWithEmailAndPassword(login, password).await()
            val uid = result.user?.uid
            val user = User(uid!!, name, surname)
            users.document(uid).set(user).await()
            ResourceNetwork.Success(EMPTY_SUCCESS)
        }
    }

    override suspend fun login(login: String, password: String) =
        withContext(Dispatchers.IO) {
            safeAuthCall(resources) {
                auth.signInWithEmailAndPassword(login, password).await()
                ResourceNetwork.Success(EMPTY_SUCCESS)
            }
        }

    override suspend fun restorePassword(login: String) =
        withContext(Dispatchers.IO) {
            safeAuthCall(resources) {
                auth.sendPasswordResetEmail(login).await()
                ResourceNetwork.Success(EMPTY_SUCCESS)
            }
        }

    override suspend fun reauthenticate(password: String) = withContext(Dispatchers.IO) {
        safeCall {
            auth.currentUser?.email?.let {
                val credentials = EmailAuthProvider.getCredential(it, password)
                auth.currentUser?.reauthenticate(credentials)?.await()
            }
            ResourceNetwork.Success(EMPTY_SUCCESS)
        }
    }

    override suspend fun updateEmail(email: String) = withContext(Dispatchers.IO) {
        safeCall {
            auth.currentUser?.updateEmail(email)
            ResourceNetwork.Success(EMPTY_SUCCESS)
        }
    }

    override suspend fun updatePassword(password: String) = withContext(Dispatchers.IO) {
        auth.currentUser?.updatePassword(password)
        ResourceNetwork.Success(EMPTY_SUCCESS)
    }

    override suspend fun logOut() = withContext(Dispatchers.IO) {
        safeCall {
            Firebase.auth.signOut()
            ResourceNetwork.Success(EMPTY_SUCCESS)
        }
    }
}