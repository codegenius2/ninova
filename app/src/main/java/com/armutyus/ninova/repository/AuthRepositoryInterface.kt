package com.armutyus.ninova.repository

import com.armutyus.ninova.constants.Response
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector

interface AuthRepositoryInterface {

    suspend fun signInWithEmailPassword(email:String , password:String): Flow<Response<Boolean>>

    suspend fun signUpWithEmailPassword(email: String , password: String)

    fun signOut()

    fun getCurrentUser(): FirebaseUser?

    suspend fun sendResetPassword(email : String) : Boolean

}