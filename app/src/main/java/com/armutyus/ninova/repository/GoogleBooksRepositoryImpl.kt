package com.armutyus.ninova.repository

import com.armutyus.ninova.R
import com.armutyus.ninova.constants.Response
import com.armutyus.ninova.constants.Util.Companion.toLocalizedString
import com.armutyus.ninova.model.googlebooksmodel.BookDetails
import com.armutyus.ninova.model.googlebooksmodel.GoogleApiBooks
import com.armutyus.ninova.service.GoogleBooksApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GoogleBooksRepositoryImpl @Inject constructor(
    private val googleBooksApiService: GoogleBooksApiService
) : GoogleBooksRepositoryInterface {

    override fun getBookDetails(bookId: String): Flow<Response<BookDetails>> =
        flow {
            emit(Response.Loading)
            val response = googleBooksApiService.getBookDetails(bookId)
            if (response.isSuccessful) {
                response.body()?.let {
                    return@let emit(Response.Success(it))
                }
                    ?: emit(Response.Failure(R.string.something_went_wrong.toLocalizedString()))
            } else {
                emit(Response.Failure(R.string.something_went_wrong.toLocalizedString()))
            }
        }.catch {
            emit(Response.Failure(R.string.error_with_message.toLocalizedString(it.localizedMessage)))
        }

    override fun searchBooksFromApi(searchQuery: String): Flow<Response<GoogleApiBooks>> =
        flow {
            emit(Response.Loading)
            val response = googleBooksApiService.searchBooks(searchQuery)
            if (response.isSuccessful) {
                response.body()?.let {
                    return@let emit(Response.Success(it))
                }
                    ?: emit(Response.Failure(R.string.something_went_wrong.toLocalizedString()))
            } else {
                emit(Response.Failure(R.string.something_went_wrong.toLocalizedString()))
            }
        }.catch {
            emit(Response.Failure(R.string.error_with_message.toLocalizedString(it.localizedMessage)))
        }
}