package com.armutyus.ninova.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.armutyus.ninova.repository.AuthRepository
import com.google.firebase.auth.AuthCredential
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    fun registerAnonymousUser(credential: AuthCredential) = liveData(Dispatchers.IO) {
        repository.anonymousToPermanent(credential).collect { response ->
            emit(response)
        }
    }

    fun createUser() = liveData(Dispatchers.IO) {
        repository.createUserInFirestore().collect { response ->
            emit(response)
        }
    }

    fun signOut() = liveData(Dispatchers.IO) {
        repository.signOut().collect { response ->
            emit(response)
        }
    }
}