package com.armutyus.ninova.constants

import com.armutyus.ninova.model.DataModel
import com.armutyus.ninova.roomdb.entities.LocalShelf

object Cache {
    var currentBook: DataModel.GoogleBookItem? = null
    var currentLocalBook: DataModel.LocalBook? = null
    var currentShelf: LocalShelf? = null
    var currentBookIdExtra: String? = null
}