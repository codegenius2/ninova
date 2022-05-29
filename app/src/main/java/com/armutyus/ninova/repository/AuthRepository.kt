package com.armutyus.ninova.repository

import com.armutyus.ninova.constants.Constants
import com.armutyus.ninova.constants.Constants.CREATED_AT
import com.armutyus.ninova.constants.Constants.EMAIL
import com.armutyus.ninova.constants.Constants.ERROR_MESSAGE
import com.armutyus.ninova.constants.Constants.NAME
import com.armutyus.ninova.constants.Constants.PHOTO_URL
import com.armutyus.ninova.constants.Constants.USERS_REF
import com.armutyus.ninova.constants.Response
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Named

class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth
) : AuthRepositoryInterface {

    @Named(USERS_REF)
    @Inject
    lateinit var usersRef: CollectionReference

    override suspend fun signInWithEmailPassword(email: String, password: String) = flow {
        try {
            emit(Response.Loading)
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            authResult.apply {
                emit(Response.Success(true))
            }
        } catch (e: Exception) {
            emit(Response.Failure(e.message ?: ERROR_MESSAGE))
        }
    }

    override suspend fun createUserInFirestore() = flow {
        try {
            emit(Response.Loading)
            auth.currentUser?.apply {
                usersRef.document(uid).set(
                    mapOf(
                        NAME to displayName,
                        EMAIL to email,
                        PHOTO_URL to photoUrl?.toString(),
                        CREATED_AT to FieldValue.serverTimestamp()
                    )
                ).await().also {
                    emit(Response.Success(it))
                }
            }
        } catch (e: Exception) {
            emit(Response.Failure(e.message ?: ERROR_MESSAGE))
        }
    }

    override suspend fun signUpWithEmailPassword(email: String, password: String) = flow {
        try {
            emit(Response.Loading)
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            authResult.apply {
                emit(Response.Success(true))
            }
        } catch (e: Exception) {
            emit(Response.Failure(e.message ?: ERROR_MESSAGE))
        }
    }

    override fun signOut() = flow {
        try {
            emit(Response.Loading)
            auth.signOut().also {
                emit(Response.Success(it))
            }
        } catch (e: Exception) {
            emit(Response.Failure(e.message ?: ERROR_MESSAGE))
        }
    }

    override fun getCurrentUser(): Boolean {
        return false
    }

    override suspend fun sendResetPassword(email: String): Boolean {
        auth.sendPasswordResetEmail(email)
        return true
    }

}