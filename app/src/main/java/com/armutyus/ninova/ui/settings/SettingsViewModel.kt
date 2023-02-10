package com.armutyus.ninova.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.armutyus.ninova.constants.Response
import com.armutyus.ninova.repository.FirebaseRepositoryInterface
import com.armutyus.ninova.repository.LocalBooksRepositoryInterface
import com.armutyus.ninova.repository.ShelfRepositoryInterface
import com.armutyus.ninova.roomdb.NinovaLocalDB
import com.google.firebase.auth.AuthCredential
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val booksRepository: LocalBooksRepositoryInterface,
    private val shelfRepository: ShelfRepositoryInterface,
    private val firebaseRepository: FirebaseRepositoryInterface,
    private val db: NinovaLocalDB
) : ViewModel() {

    fun uploadUserData(
        onComplete: (Response<Boolean>) -> Unit
    ) = viewModelScope.launch {
        val localBooks = booksRepository.getLocalBooks()
        localBooks.forEach {
            val response = firebaseRepository.uploadUserBooksToFirestore(it)
            if (response is Response.Failure) {
                onComplete(response)
                return@launch
            }
        }

        val localCrossRef = booksRepository.getBookShelfCrossRef()
        localCrossRef.forEach {
            val response = firebaseRepository.uploadUserCrossRefToFirestore(it)
            if (response is Response.Failure) {
                onComplete(response)
                return@launch
            }
        }

        val localShelf = shelfRepository.getLocalShelves()
        localShelf.forEach {
            val response = firebaseRepository.uploadUserShelvesToFirestore(it)
            if (response is Response.Failure) {
                onComplete(response)
                return@launch
            }
        }
        onComplete(Response.Success(true))
    }

    fun deleteUserPermanently(credential: AuthCredential, onComplete: (Response<Boolean>) -> Unit) =
        viewModelScope.launch {
            val reAuthResponse = firebaseRepository.reAuthUser(credential)
            if (reAuthResponse is Response.Failure) {
                onComplete(reAuthResponse)
                return@launch
            }

            val emailResponse = firebaseRepository.deleteUserPermanently()
            if (emailResponse is Response.Failure) {
                onComplete(emailResponse)
                return@launch
            }
            onComplete(Response.Success(true))
        }

    fun signOut(onComplete: (Response<Boolean>) -> Unit) = viewModelScope.launch {
        val response = firebaseRepository.signOut()
        onComplete(response)
    }

    fun clearDatabase() {
        CoroutineScope(Dispatchers.IO).launch {
            db.clearAllTables()
        }
    }

}