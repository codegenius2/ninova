package com.armutyus.ninova.roomdb.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.armutyus.ninova.ui.shelves.ShelvesViewModel

@Entity(tableName = "Shelf")
data class LocalShelf(
    @PrimaryKey(autoGenerate = true) var shelfId: Int,
    var shelfTitle: String?,
    var createdAt: String?,
    var shelfCover: String?,
) {
    fun getBookCount(shelvesViewModel: ShelvesViewModel): Int {
        return shelvesViewModel.shelfWithBooksList.value?.firstOrNull {
            it.shelf.shelfId == shelfId
        }?.booksCount ?: 0
    }
}