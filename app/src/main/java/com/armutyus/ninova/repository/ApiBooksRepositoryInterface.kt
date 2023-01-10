package com.armutyus.ninova.repository

import com.armutyus.ninova.constants.Response
import com.armutyus.ninova.model.BookDetails
import com.armutyus.ninova.model.GoogleApiBooks
import kotlinx.coroutines.flow.Flow

interface ApiBooksRepositoryInterface {

    suspend fun getBookDetails(bookId: String): Flow<Response<BookDetails>>

    suspend fun searchBooksFromApi(searchQuery: String): Flow<Response<GoogleApiBooks>>

}