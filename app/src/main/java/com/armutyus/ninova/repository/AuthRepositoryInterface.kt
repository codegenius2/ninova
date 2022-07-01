package com.armutyus.ninova.repository

import com.armutyus.ninova.constants.Response
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow

interface AuthRepositoryInterface {

    suspend fun signInWithEmailPassword(email: String, password: String): Flow<Response<Boolean>>

    suspend fun signInAnonymous(): Flow<Response<Boolean>>

    suspend fun anonymousToPermanent(credential: AuthCredential): Flow<Response<Boolean>>

    suspend fun signUpWithEmailPassword(email: String, password: String): Flow<Response<Boolean>>

    suspend fun createUserInFirestore(): Flow<Response<Void>>

    suspend fun signOut(): Flow<Response<Unit>>

    suspend fun reAuthUser(credential: AuthCredential): Flow<Response<Boolean>>

    suspend fun changeUserEmail(email: String): Flow<Response<Boolean>>

    suspend fun changeUserPassword(password: String): Flow<Response<Boolean>>

    suspend fun sendResetPassword(email: String): Flow<Response<Boolean>>

    fun getCurrentUser(): FirebaseUser?

}