package com.armutyus.ninova.repository

import com.armutyus.ninova.constants.Constants.CREATED_AT
import com.armutyus.ninova.constants.Constants.EMAIL
import com.armutyus.ninova.constants.Constants.ERROR_MESSAGE
import com.armutyus.ninova.constants.Constants.NAME
import com.armutyus.ninova.constants.Constants.PHOTO_URL
import com.armutyus.ninova.constants.Constants.USERS_REF
import com.armutyus.ninova.constants.Response
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val db: FirebaseFirestore
) : AuthRepositoryInterface {

    override suspend fun signInWithEmailPassword(email: String, password: String) = flow {
        try {
            emit(Response.Loading)
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            authResult.apply {
                emit(Response.Success(true))
            }
        } catch (e: Exception) {
            emit(Response.Failure(e.localizedMessage ?: ERROR_MESSAGE))
        }
    }

    override suspend fun anonymousToPermanent(credential: AuthCredential) = flow {
        try {
            emit(Response.Loading)
            val authResult = auth.currentUser!!.linkWithCredential(credential).await()
            authResult.apply {
                emit(Response.Success(true))
            }
        } catch (e: Exception) {
            emit(Response.Failure(e.localizedMessage ?: ERROR_MESSAGE))
        }
    }

    override suspend fun signInAnonymous() = flow {
        try {
            emit(Response.Loading)
            val authResult = auth.signInAnonymously().await()
            authResult.apply {
                emit(Response.Success(true))
            }
        } catch (e: Exception) {
            emit(Response.Failure(e.localizedMessage ?: ERROR_MESSAGE))
        }
    }

    override suspend fun createUserInFirestore() = flow {
        try {
            emit(Response.Loading)
            auth.currentUser?.apply {
                db.collection(USERS_REF).document(uid).set(
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
            emit(Response.Failure(e.localizedMessage ?: ERROR_MESSAGE))
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
            emit(Response.Failure(e.localizedMessage ?: ERROR_MESSAGE))
        }
    }

    override suspend fun signOut() = flow {
        try {
            emit(Response.Loading)
            auth.signOut().also {
                emit(Response.Success(it))
            }
        } catch (e: Exception) {
            emit(Response.Failure(e.localizedMessage ?: ERROR_MESSAGE))
        }
    }

    override suspend fun reAuthUser(credential: AuthCredential) = flow {
        try {
            emit(Response.Loading)
            val reAuthResult = auth.currentUser!!.reauthenticate(credential).await()
            reAuthResult.apply {
                emit(Response.Success(true))
            }
        } catch (e: Exception) {
            emit(Response.Failure(e.localizedMessage ?: ERROR_MESSAGE))
        }
    }

    override suspend fun changeUserEmail(email: String) = flow {
        try {
            emit(Response.Loading)
            val reAuthResult = auth.currentUser!!.updateEmail(email).await()
            reAuthResult.apply {
                emit(Response.Success(true))
            }
        } catch (e: Exception) {
            emit(Response.Failure(e.localizedMessage ?: ERROR_MESSAGE))
        }
    }

    override suspend fun changeUserPassword(password: String) = flow {
        try {
            emit(Response.Loading)
            val reAuthResult = auth.currentUser!!.updatePassword(password).await()
            reAuthResult.apply {
                emit(Response.Success(true))
            }
        } catch (e: Exception) {
            emit(Response.Failure(e.localizedMessage ?: ERROR_MESSAGE))
        }
    }

    override suspend fun sendResetPassword(email: String) = flow {
        try {
            emit(Response.Loading)
            auth.sendPasswordResetEmail(email).await().apply {
                emit(Response.Success(true))
            }
        } catch (e: Exception) {
            emit(Response.Failure(e.localizedMessage ?: ERROR_MESSAGE))
        }
    }


    override fun getCurrentUser(): FirebaseUser? {
        return auth.currentUser
    }

}