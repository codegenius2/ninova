package com.armutyus.ninova.repository

import com.armutyus.ninova.model.DataModel
import com.armutyus.ninova.roomdb.NinovaDao
import com.armutyus.ninova.roomdb.entities.BookShelfCrossRef
import com.armutyus.ninova.roomdb.entities.BookWithShelves
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LocalBooksRepositoryImpl @Inject constructor(
    private val ninovaDao: NinovaDao,
    private val coroutineContext: CoroutineDispatcher = Dispatchers.IO
) : LocalBooksRepositoryInterface {

    override suspend fun searchLocalBooks(searchString: String): List<DataModel.LocalBook> =
        withContext(coroutineContext) {
            ninovaDao.searchLocalBooks(searchString)
        }

    override suspend fun delete(localBook: DataModel.LocalBook) = withContext(coroutineContext) {
        ninovaDao.deleteBook(localBook)
    }

    override suspend fun deleteBookById(id: String) = withContext(coroutineContext) {
        ninovaDao.deleteBookById(id)
    }

    override suspend fun insert(localBook: DataModel.LocalBook) = withContext(coroutineContext) {
        ninovaDao.insertBook(localBook)
    }

    override suspend fun update(localBook: DataModel.LocalBook) = withContext(coroutineContext) {
        ninovaDao.updateBook(localBook)
    }

    override suspend fun getLocalBooks(): List<DataModel.LocalBook> =
        withContext(coroutineContext) {
            ninovaDao.getLocalBooks()
        }

    override suspend fun getBookWithShelves(bookId: String): List<BookWithShelves> =
        withContext(coroutineContext) {
            ninovaDao.getShelvesOfBook(bookId)
        }

    override suspend fun getBookShelfCrossRef(): List<BookShelfCrossRef> =
        withContext(coroutineContext) {
            ninovaDao.getBookShelfCrossRef()
        }

}