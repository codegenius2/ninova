package com.armutyus.ninova.repository

import com.google.firebase.auth.FirebaseUser

interface FirebaseAuthenticatorInterface {

    suspend fun signUpWithEmailPassword(email:String , password:String) : FirebaseUser?

    suspend fun signInWithEmailPassword(email: String , password: String): FirebaseUser?

    fun signOut() : FirebaseUser?

    fun getUser() : FirebaseUser?

    suspend fun sendPasswordReset(email :String)

}