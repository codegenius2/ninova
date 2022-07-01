package com.armutyus.ninova.roomdb.entities

import androidx.room.Entity

@Entity(primaryKeys = ["bookId", "shelfId"])
data class BookShelfCrossRef(
    val bookId: Int,
    val shelfId: Int
)