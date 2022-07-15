package com.armutyus.ninova.model

import com.armutyus.ninova.ui.books.BooksViewModel

data class Book(
    val bookTitle: String,
    val bookAuthor: List<String>,
    val bookPages: String,
    val releaseDate: String
) {
    fun isBookAddedCheck(booksViewModel: BooksViewModel): Boolean {
        val searchBookList = booksViewModel.bookList.value?.find { it.bookTitle == bookTitle }
        return searchBookList != null
    }
}
