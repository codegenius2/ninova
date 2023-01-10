package com.armutyus.ninova.repository

import com.armutyus.ninova.constants.Response
import com.armutyus.ninova.model.BookDetails
import com.armutyus.ninova.model.GoogleApiBooks
import com.armutyus.ninova.service.GoogleBooksApiService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class ApiBooksRepositoryImpl @Inject constructor(
    private val googleBooksApiService: GoogleBooksApiService,
    private val coroutineContext: CoroutineDispatcher = Dispatchers.IO
) : ApiBooksRepositoryInterface {

    override suspend fun getBookDetails(bookId: String): Flow<Response<BookDetails>> =
        withContext(coroutineContext) {
            flow {
                try {
                    emit(Response.Loading)
                    val response = googleBooksApiService.getBookDetails(bookId)
                    if (response.isSuccessful) {
                        response.body()?.let {
                            return@let emit(Response.Success(it))
                        } ?: emit(Response.Failure("Something went wrong!"))
                    } else {
                        emit(Response.Failure("Something went wrong!"))
                    }
                } catch (e: Exception) {
                    emit(Response.Failure("Error: ${e.localizedMessage}"))
                }
            }
        }

    override suspend fun searchBooksFromApi(searchQuery: String): Flow<Response<GoogleApiBooks>> =
        withContext(coroutineContext) {
            flow {
                try {
                    emit(Response.Loading)
                    val response = googleBooksApiService.searchBooks(searchQuery)
                    if (response.isSuccessful) {
                        response.body()?.let {
                            return@let emit(Response.Success(it))
                        } ?: emit(Response.Failure("Something went wrong!"))
                    } else {
                        emit(Response.Failure("Something went wrong!"))
                    }
                } catch (e: Exception) {
                    emit(Response.Failure("Error: ${e.localizedMessage}"))
                }
            }
        }
}