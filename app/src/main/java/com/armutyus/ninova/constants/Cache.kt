package com.armutyus.ninova.constants

import com.armutyus.ninova.model.googlebooksmodel.DataModel
import com.armutyus.ninova.model.openlibrarymodel.OpenLibraryWork
import com.armutyus.ninova.roomdb.entities.LocalShelf

object Cache {
    var currentGoogleBook: DataModel.GoogleBookItem? = null
    var currentLocalBook: DataModel.LocalBook? = null
    var currentOpenLibBook: OpenLibraryWork? = null
    var currentOpenLibBookCategory: List<String>? = null
    var currentShelf: LocalShelf? = null
    var currentBookIdExtra: String? = null
}