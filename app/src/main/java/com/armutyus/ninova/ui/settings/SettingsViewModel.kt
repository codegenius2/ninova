package com.armutyus.ninova.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.armutyus.ninova.constants.Response
import com.armutyus.ninova.repository.FirebaseRepositoryInterface
import com.armutyus.ninova.repository.LocalBooksRepositoryInterface
import com.armutyus.ninova.repository.ShelfRepositoryInterface
import com.armutyus.ninova.roomdb.NinovaLocalDB
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val booksRepository: LocalBooksRepositoryInterface,
    private val shelfRepository: ShelfRepositoryInterface,
    private val repository: FirebaseRepositoryInterface,
    private val db: NinovaLocalDB
) : ViewModel() {

    fun uploadUserData(
        onComplete: (Response<Boolean>) -> Unit
    ) = viewModelScope.launch {
        val localBooks = booksRepository.getLocalBooks()
        localBooks.forEach {
            val response = repository.uploadUserBooksToFirestore(it)
            if (response is Response.Failure) {
                onComplete(response)
                return@launch
            }
        }

        val localCrossRef = booksRepository.getBookShelfCrossRef()
        localCrossRef.forEach {
            val response = repository.uploadUserCrossRefToFirestore(it)
            if (response is Response.Failure) {
                onComplete(response)
                return@launch
            }
        }

        val localShelf = shelfRepository.getLocalShelves()
        localShelf.forEach {
            val response = repository.uploadUserShelvesToFirestore(it)
            if (response is Response.Failure) {
                onComplete(response)
                return@launch
            }
        }
        onComplete(Response.Success(true))
    }

    fun signOut(onComplete: (Response<Boolean>) -> Unit) = viewModelScope.launch {
        val response = repository.signOut()
        onComplete(response)
    }

    fun clearDatabase() {
        CoroutineScope(Dispatchers.IO).launch {
            db.clearAllTables()
        }
    }

}