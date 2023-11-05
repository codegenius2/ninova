package com.armutyus.ninova.model.openlibrarymodel

sealed class BookDetailsResponse {

    data class BookKeyResponse(
        val description: String?
    )

    data class BookLendingKeyResponse(
        val number_of_pages: String?,
        val publishers: List<String>?
    )

    data class CombinedResponse(
        val description: String? = null,
        val number_of_pages: String? = null,
        val publishers: List<String>? = null,
        val loading: Boolean = false,
        val keyError: String? = null,
        val lendingKeyError: String? = null
    )

}
