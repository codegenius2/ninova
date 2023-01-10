package com.armutyus.ninova.ui.shelves

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.armutyus.ninova.constants.Response
import com.armutyus.ninova.repository.FirebaseRepositoryInterface
import com.armutyus.ninova.repository.ShelfRepositoryInterface
import com.armutyus.ninova.roomdb.entities.BookShelfCrossRef
import com.armutyus.ninova.roomdb.entities.LocalShelf
import com.armutyus.ninova.roomdb.entities.ShelfWithBooks
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ShelvesViewModel @Inject constructor(
    private val shelfRepositoryInterface: ShelfRepositoryInterface,
    private val firebaseRepository: FirebaseRepositoryInterface
) : ViewModel() {
    //Local Shelf Works

    private val _currentShelfList = MutableLiveData<List<LocalShelf>>()
    val currentShelfList: LiveData<List<LocalShelf>>
        get() = _currentShelfList

    private val _shelfList = MutableLiveData<List<LocalShelf>>()
    val shelfList: LiveData<List<LocalShelf>>
        get() = _shelfList

    private val _searchShelvesList = MutableLiveData<List<LocalShelf>>()
    val searchShelvesList: LiveData<List<LocalShelf>>
        get() = _searchShelvesList

    private val _shelfWithBooksList = MutableLiveData<List<ShelfWithBooks>>()
    val shelfWithBooksList: LiveData<List<ShelfWithBooks>>
        get() = _shelfWithBooksList

    fun setCurrentList(shelfList: List<LocalShelf>) {
        _currentShelfList.value = shelfList
    }

    fun loadShelfList() = viewModelScope.launch {
        _shelfList.value = shelfRepositoryInterface.getLocalShelves()
    }

    fun insertShelf(localShelf: LocalShelf) = viewModelScope.launch {
        shelfRepositoryInterface.insert(localShelf)
    }

    fun updateShelf(localShelf: LocalShelf) = viewModelScope.launch {
        shelfRepositoryInterface.update(localShelf)
    }

    fun deleteShelf(localShelf: LocalShelf) = viewModelScope.launch {
        shelfRepositoryInterface.delete(localShelf)
    }

    fun insertBookShelfCrossRef(crossRef: BookShelfCrossRef) = viewModelScope.launch {
        shelfRepositoryInterface.insertBookShelfCrossRef(crossRef)
    }

    fun deleteBookShelfCrossRef(crossRef: BookShelfCrossRef) = viewModelScope.launch {
        shelfRepositoryInterface.deleteBookShelfCrossRef(crossRef)
    }

    fun searchShelves(searchString: String) = viewModelScope.launch {
        _searchShelvesList.value = shelfRepositoryInterface.searchLocalShelves(searchString)
    }

    fun loadShelfWithBookList() = viewModelScope.launch {
        _shelfWithBooksList.value = shelfRepositoryInterface.getShelfWithBooks()
    }

    // Firebase Works

    fun deleteCrossRefFromFirestore(crossRefId: String, onComplete: (Response<Boolean>) -> Unit) =
        viewModelScope.launch {
            val response = firebaseRepository.deleteUserCrossRefFromFirestore(crossRefId)
            onComplete(response)
        }

    fun deleteShelfFromFirestore(shelfId: String, onComplete: (Response<Boolean>) -> Unit) =
        viewModelScope.launch {
            val response = firebaseRepository.deleteUserShelfFromFirestore(shelfId)
            onComplete(response)
        }

    fun uploadShelfToFirestore(
        localShelf: LocalShelf,
        onComplete: (Response<Boolean>) -> Unit
    ) =
        viewModelScope.launch {
            val response = firebaseRepository.uploadUserShelvesToFirestore(localShelf)
            onComplete(response)
        }

    fun uploadCrossRefToFirestore(
        crossRef: BookShelfCrossRef,
        onComplete: (Response<Boolean>) -> Unit
    ) =
        viewModelScope.launch {
            val response = firebaseRepository.uploadUserCrossRefToFirestore(crossRef)
            onComplete(response)
        }

}