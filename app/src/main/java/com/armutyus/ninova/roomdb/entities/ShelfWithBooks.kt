package com.armutyus.ninova.roomdb.entities

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.armutyus.ninova.model.DataModel

data class ShelfWithBooks(
    @Embedded val shelf: LocalShelf,
    @Relation(
        parentColumn = "shelfId",
        entityColumn = "bookId",
        associateBy = Junction(BookShelfCrossRef::class)
    )
    val bookList: List<DataModel.LocalBook>
) {
    val booksCount: Int
        get() = bookList.size
}