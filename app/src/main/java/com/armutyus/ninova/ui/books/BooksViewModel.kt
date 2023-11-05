package com.armutyus.ninova.ui.books

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.armutyus.ninova.constants.Response
import com.armutyus.ninova.model.googlebooksmodel.BookDetails
import com.armutyus.ninova.model.googlebooksmodel.DataModel
import com.armutyus.ninova.repository.FirebaseRepositoryInterface
import com.armutyus.ninova.repository.GoogleBooksRepositoryInterface
import com.armutyus.ninova.repository.LocalBooksRepositoryInterface
import com.armutyus.ninova.roomdb.entities.BookShelfCrossRef
import com.armutyus.ninova.roomdb.entities.BookWithShelves
import com.armutyus.ninova.roomdb.entities.LocalShelf
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BooksViewModel @Inject constructor(
    private val apiBooksRepository: GoogleBooksRepositoryInterface,
    private val booksRepository: LocalBooksRepositoryInterface,
    private val firebaseRepository: FirebaseRepositoryInterface
) : ViewModel() {
    // Google Book Works

    private val _bookDetails = MutableLiveData<Response<BookDetails>>()
    val bookDetails: LiveData<Response<BookDetails>>
        get() = _bookDetails

    fun getBookDetailsById(id: String) = viewModelScope.launch {
        apiBooksRepository.getBookDetails(id).collectLatest { response ->
            _bookDetails.postValue(response)
        }
    }

    // Local Book Works

    private val _bookShelfCrossRefList = MutableLiveData<List<BookShelfCrossRef>>()
    val bookShelfCrossRefList: LiveData<List<BookShelfCrossRef>>
        get() = _bookShelfCrossRefList

    private val _bookWithShelvesList = MutableLiveData<List<BookWithShelves>>()
    val bookWithShelvesList: LiveData<List<BookWithShelves>>
        get() = _bookWithShelvesList

    private val _localBookList = MutableLiveData<List<DataModel.LocalBook>>()
    val localBookList: LiveData<List<DataModel.LocalBook>>
        get() = _localBookList

    fun deleteBook(localBook: DataModel.LocalBook) = viewModelScope.launch {
        booksRepository.delete(localBook)
    }

    fun deleteBookById(id: String) = viewModelScope.launch {
        booksRepository.deleteBookById(id)
    }

    fun insertBook(localBook: DataModel.LocalBook) = viewModelScope.launch {
        booksRepository.insert(localBook)
    }

    fun updateBook(localBook: DataModel.LocalBook) = viewModelScope.launch {
        booksRepository.update(localBook)
    }

    fun loadBookList() = viewModelScope.launch {
        _localBookList.value = booksRepository.getLocalBooks()
    }


    fun loadBookWithShelves(bookId: String) = viewModelScope.launch {
        _bookWithShelvesList.value = booksRepository.getBookWithShelves(bookId)
    }

    fun loadBookShelfCrossRef() = viewModelScope.launch {
        _bookShelfCrossRefList.value = booksRepository.getBookShelfCrossRef()
    }

    //Firebase Works

    fun collectBooksFromFirestore(onComplete: (Response<List<DataModel.LocalBook>>) -> Unit) =
        viewModelScope.launch {
            val response = firebaseRepository.downloadUserBooksFromFirestore()
            onComplete(response)
        }

    fun collectCrossRefFromFirestore(onComplete: (Response<List<BookShelfCrossRef>>) -> Unit) =
        viewModelScope.launch {
            val response = firebaseRepository.downloadUserCrossRefFromFirestore()
            onComplete(response)
        }

    fun collectShelvesFromFirestore(onComplete: (Response<List<LocalShelf>>) -> Unit) =
        viewModelScope.launch {
            val response = firebaseRepository.downloadUserShelvesFromFirestore()
            onComplete(response)
        }

    fun deleteBookFromFirestore(bookId: String, onComplete: (Response<Boolean>) -> Unit) =
        viewModelScope.launch {
            val response = firebaseRepository.deleteUserBookFromFirestore(bookId)
            onComplete(response)
        }

    fun uploadCustomBookCoverToFirestore(uri: Uri, onComplete: (Response<Uri>) -> Unit) =
        viewModelScope.launch {
            val response = firebaseRepository.uploadCustomBookCoverToFirestore(uri)
            onComplete(response)
        }

    fun uploadBookToFirestore(
        localBook: DataModel.LocalBook,
        onComplete: (Response<Boolean>) -> Unit
    ) =
        viewModelScope.launch {
            val response = firebaseRepository.uploadUserBooksToFirestore(localBook)
            onComplete(response)
        }
}