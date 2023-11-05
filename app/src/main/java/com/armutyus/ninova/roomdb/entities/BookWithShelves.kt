package com.armutyus.ninova.roomdb.entities

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.armutyus.ninova.model.googlebooksmodel.DataModel

data class BookWithShelves(
    @Embedded val book: DataModel.LocalBook,
    @Relation(
        parentColumn = "bookId",
        entityColumn = "shelfId",
        associateBy = Junction(BookShelfCrossRef::class)
    )
    val shelfList: List<LocalShelf>
)