package com.armutyus.ninova.repository

import android.util.Log
import com.armutyus.ninova.R
import com.armutyus.ninova.constants.Response
import com.armutyus.ninova.constants.Util.Companion.toLocalizedString
import com.armutyus.ninova.model.openlibrarymodel.BookDetailsResponse
import com.armutyus.ninova.model.openlibrarymodel.OpenLibraryResponse
import com.armutyus.ninova.service.OpenLibraryApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class OpenLibRepositoryImpl @Inject constructor(
    private val openLibraryApiService: OpenLibraryApiService
) : OpenLibRepositoryInterface {
    override fun getBooksByCategory(
        category: String,
        offset: Int
    ): Flow<Response<OpenLibraryResponse>> =
        flow {
            val categoryUrl = if (category.contains(" ")) {
                category.replace(" ", "_")
            } else {
                category
            }
            val fixedUrl = "subjects/$categoryUrl.json?sort=rating&limit=50&offset=$offset"
            emit(Response.Loading)
            val response = openLibraryApiService.getBooksByCategory(fixedUrl)
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

    override fun getBookKeyDetails(
        bookKey: String
    ): Flow<Response<BookDetailsResponse.BookKeyResponse>> =
        flow {
            val fixedUrl = "works/$bookKey.json"
            emit(Response.Loading)
            val response = openLibraryApiService.getBookKeyDetails(fixedUrl)
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

    override fun getBookLendingDetails(
        bookLendingKey: String
    ): Flow<Response<BookDetailsResponse.BookLendingKeyResponse>> =
        flow {
            val fixedUrl = "books/$bookLendingKey.json"
            emit(Response.Loading)
            val response = openLibraryApiService.getBookLendingDetails(fixedUrl)
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

    override fun getRandomBookCoverForCategory(category: String): Flow<Response<String>> =
        flow {
            emit(Response.Loading)
            val fixedUrl = "subjects/$category.json?sort=rating&limit=30"
            val response = openLibraryApiService.getBooksByCategory(fixedUrl)
            if (response.isSuccessful) {
                response.body()?.let {
                    return@let emit(Response.Success(it.works.random().cover_id))
                }
                    ?: emit(Response.Failure(R.string.something_went_wrong.toLocalizedString()))
            } else {
                emit(Response.Failure(R.string.something_went_wrong.toLocalizedString()))
            }
        }.catch {
            Log.i(
                "CategoryCoverError",
                R.string.error_with_message.toLocalizedString(it.localizedMessage)
            )
            emit(Response.Failure(R.string.something_went_wrong.toLocalizedString()))
        }
}