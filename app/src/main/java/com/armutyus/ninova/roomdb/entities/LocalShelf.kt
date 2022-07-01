package com.armutyus.ninova.roomdb.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Shelf")
data class LocalShelf(
    @PrimaryKey(autoGenerate = true) val shelfId: Int,
    val shelfTitle: String?,
    val createdAt: String?,
    val shelfCover: String?,
    var booksInShelf: Int?
)