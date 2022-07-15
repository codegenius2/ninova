package com.armutyus.ninova.ui.books

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.armutyus.ninova.repository.BooksRepositoryInterface
import com.armutyus.ninova.roomdb.entities.BookWithShelves
import com.armutyus.ninova.roomdb.entities.LocalBook
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BooksViewModel @Inject constructor(
    private val booksRepositoryInterface: BooksRepositoryInterface
) : ViewModel() {

    private val mainBookList = MutableLiveData<List<LocalBook>>()
    val bookList: LiveData<List<LocalBook>>
        get() = mainBookList

    private val _bookWithShelvesList = MutableLiveData<List<BookWithShelves>>()
    val bookWithShelvesList: LiveData<List<BookWithShelves>>
        get() = _bookWithShelvesList

    fun getBookList() {
        CoroutineScope(Dispatchers.IO).launch {
            booksRepositoryInterface.getLocalBooks().collectLatest {
                mainBookList.postValue(it)
            }
        }
    }

    fun insertBook(localBook: LocalBook) = CoroutineScope(Dispatchers.IO).launch {
        booksRepositoryInterface.insert(localBook)
    }

    fun updateBook(localBook: LocalBook) = CoroutineScope(Dispatchers.IO).launch {
        booksRepositoryInterface.update(localBook)
    }

    fun deleteBook(localBook: LocalBook) = viewModelScope.launch {
        booksRepositoryInterface.delete(localBook)
    }

    fun getBookWithShelves(bookId: Int) {
        CoroutineScope(Dispatchers.IO).launch {
            booksRepositoryInterface.getBookWithShelves(bookId).collectLatest {
                _bookWithShelvesList.postValue(it)
            }
        }
    }

}