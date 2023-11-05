package com.armutyus.ninova.service

import com.armutyus.ninova.model.openlibrarymodel.BookDetailsResponse
import com.armutyus.ninova.model.openlibrarymodel.OpenLibraryResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path

interface OpenLibraryApiService {
    @Headers(
        "Accept: application/json",
        "Content-Type: application/json",
        "Platform: android"
    )
    @GET("{url}")
    suspend fun getBooksByCategory(
        @Path("url") fixedUrl: String,
    ): Response<OpenLibraryResponse>

    @GET("{url}")
    suspend fun getBookKeyDetails(
        @Path("url") fixedUrl: String,
    ): Response<BookDetailsResponse.BookKeyResponse>

    @GET("{url}")
    suspend fun getBookLendingDetails(
        @Path("url") fixedUrl: String,
    ): Response<BookDetailsResponse.BookLendingKeyResponse>
}