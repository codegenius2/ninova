package com.armutyus.ninova.roomdb

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Shelf")
data class LocalShelf(
    @PrimaryKey(autoGenerate = true)
    val shelfId: Int,
    val shelfTitle: String?,
)