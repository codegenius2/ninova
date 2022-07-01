package com.armutyus.ninova.repository

import com.armutyus.ninova.model.Books
import kotlinx.coroutines.flow.Flow

interface BooksRepositoryInterface {

    fun getBooksList(): List<Books>

    fun searchBooksFromLocal(searchString: String): List<Books>

    fun searchBooksFromApi(searchString: String): List<Books>

}