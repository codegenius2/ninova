package com.armutyus.ninova.model.openlibrarymodel

data class OpenLibraryResponse(
    val work_count: Int,
    val works: List<OpenLibraryWork>
)