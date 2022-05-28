package com.armutyus.ninova.repository

import com.armutyus.ninova.constants.Constants.USERS_REF
import com.armutyus.ninova.constants.Response
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.CollectionReference
import kotlinx.coroutines.flow.flow
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Named

class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    @Named(USERS_REF) private val usersRef: CollectionReference
) : AuthRepositoryInterface {

    override suspend fun signInWithEmailPassword(email: String, password: String): Response<Boolean> {
        return try {
            val response = auth.signInWithEmailAndPassword(email, password)
            if (response.isSuccessful) {
                return Response.success(response.result.additionalUserInfo?.isNewUser)
            } else {
                Response.error("Error", null)
            }
        } catch (e: Exception) {
            Response.error("Login failed!", null)
        }
    }

    override suspend fun signUpWithEmailPassword(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
    }

    override fun signOut() {
        auth.signOut()
    }

    override fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

    override suspend fun sendResetPassword(email: String): Boolean {
        auth.sendPasswordResetEmail(email)
        return true
    }

}