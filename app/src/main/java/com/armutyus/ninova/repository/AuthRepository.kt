package com.armutyus.ninova.repository

import com.armutyus.ninova.constants.Constants
import com.armutyus.ninova.constants.Response
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth
) : AuthRepositoryInterface {

    override suspend fun signInWithEmailPassword(email: String, password: String) = flow {
        try {
            emit(Response.loading(null))
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            authResult.user?.apply {
                emit(Response.success(true))
            }
        } catch (e: Exception) {
            Response.error(e.localizedMessage ?: Constants.ERROR_MESSAGE, null)
        }
    }

    override suspend fun signUpWithEmailPassword(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
    }

    override fun signOut() {
        auth.signOut()
        getCurrentUser()
    }

    override fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    override suspend fun sendResetPassword(email: String): Boolean {
        auth.sendPasswordResetEmail(email)
        return true
    }

}