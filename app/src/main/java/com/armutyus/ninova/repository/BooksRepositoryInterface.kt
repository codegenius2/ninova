package com.armutyus.ninova.repository

import com.armutyus.ninova.model.Book
import com.armutyus.ninova.roomdb.LocalBook
import com.armutyus.ninova.model.Books
import kotlinx.coroutines.flow.Flow

interface BooksRepositoryInterface {

    fun getBookList(): List<Book>

    fun searchBookFromLocal(searchString: String): List<Book>

    fun searchBookFromApi(searchString: String): List<Book>

    suspend fun insert(localBook: LocalBook)

    suspend fun update(localBook: LocalBook)

    suspend fun delete(localBook: LocalBook)

    fun getLocalBooks(): Flow<List<LocalBook>>

    fun searchLocalBooks(searchString: String): Flow<List<LocalBook>>
    fun getBooksList(): List<Books>

    fun searchBooksFromLocal(searchString: String): List<Books>

    fun searchBooksFromApi(searchString: String): List<Books>

}