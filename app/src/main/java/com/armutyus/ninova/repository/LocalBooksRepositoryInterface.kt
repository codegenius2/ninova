package com.armutyus.ninova.repository

import com.armutyus.ninova.model.googlebooksmodel.DataModel
import com.armutyus.ninova.roomdb.entities.BookShelfCrossRef
import com.armutyus.ninova.roomdb.entities.BookWithShelves

interface LocalBooksRepositoryInterface {

    suspend fun delete(localBook: DataModel.LocalBook)

    suspend fun deleteBookById(id: String)

    suspend fun insert(localBook: DataModel.LocalBook)

    suspend fun update(localBook: DataModel.LocalBook)

    suspend fun getLocalBooks(): List<DataModel.LocalBook>

    suspend fun searchLocalBooks(searchString: String): List<DataModel.LocalBook>

    suspend fun getBookWithShelves(bookId: String): List<BookWithShelves>

    suspend fun getBookShelfCrossRef(): List<BookShelfCrossRef>

}