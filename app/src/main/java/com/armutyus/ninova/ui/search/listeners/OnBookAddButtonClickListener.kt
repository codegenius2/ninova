package com.armutyus.ninova.ui.search.listeners

import com.armutyus.ninova.roomdb.LocalBook

interface OnBookAddButtonClickListener {
    fun onClick(localBook: LocalBook)
}