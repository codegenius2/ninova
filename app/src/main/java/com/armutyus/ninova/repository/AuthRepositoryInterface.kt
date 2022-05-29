package com.armutyus.ninova.repository

import com.armutyus.ninova.constants.Response
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface AuthRepositoryInterface {

    suspend fun signInWithEmailPassword(email: String, password: String): Flow<Response<Boolean>>

    suspend fun signUpWithEmailPassword(email: String, password: String): Flow<Response<Boolean>>

    suspend fun createUserInFirestore(): Flow<Response<Void>>

    fun signOut(): Flow<Response<Unit>>

    fun getCurrentUser(): FirebaseUser?

    suspend fun sendResetPassword(email: String): Boolean

}