package com.armutyus.ninova.repository

import com.armutyus.ninova.constants.Response
import com.armutyus.ninova.model.openlibrarymodel.BookDetailsResponse
import com.armutyus.ninova.model.openlibrarymodel.OpenLibraryResponse
import kotlinx.coroutines.flow.Flow

interface OpenLibRepositoryInterface {

    fun getBooksByCategory(
        category: String,
        offset: Int
    ): Flow<Response<OpenLibraryResponse>>

    fun getBookKeyDetails(bookKey: String): Flow<Response<BookDetailsResponse.BookKeyResponse>>

    fun getBookLendingDetails(bookLendingKey: String): Flow<Response<BookDetailsResponse.BookLendingKeyResponse>>

    fun getRandomBookCoverForCategory(category: String): Flow<Response<String>>

}