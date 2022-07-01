package com.armutyus.ninova.roomdb.entities

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class BookWithShelves(
    @Embedded val book: LocalBook,
    @Relation(
        parentColumn = "bookId",
        entityColumn = "shelfId",
        associateBy = Junction(BookShelfCrossRef::class)
    )
    val shelf: List<LocalShelf>
)