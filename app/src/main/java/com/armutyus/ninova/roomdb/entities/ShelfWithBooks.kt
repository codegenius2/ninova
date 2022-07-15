package com.armutyus.ninova.roomdb.entities

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class ShelfWithBooks(
    @Embedded val shelf: LocalShelf,
    @Relation(
        parentColumn = "shelfId",
        entityColumn = "bookId",
        associateBy = Junction(BookShelfCrossRef::class)
    )
    val book: List<LocalBook>
) {
    val booksCount: Int
        get() = book.size
}