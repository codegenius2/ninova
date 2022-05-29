package com.armutyus.ninova.ui.main

import com.armutyus.ninova.constants.Constants
import com.armutyus.ninova.constants.Response
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MainScreenRepository @Inject constructor(
    private val auth: FirebaseAuth
) {
    fun signOut() = flow {
        try {
            emit(Response.Loading)
            auth.signOut().also {
                emit(Response.Success(it))
            }
        } catch (e: Exception) {
            emit(Response.Failure(e.message ?: Constants.ERROR_MESSAGE))
        }
    }
}