package com.armutyus.ninova.roomdb.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Book")
data class LocalBook(
    @PrimaryKey(autoGenerate = true) var bookId: Int,
    var bookTitle: String?,
    var bookSubtitle: String?,
    var bookAuthors: List<String>?,
    var bookPages: String?,
    var bookCoverUrl: String?,
    var bookDescription: String?,
    var bookPublishedDate: String?,
    var bookCategories: List<String>?,
    var bookPublisher: String?,
    var bookNotes: String?
)