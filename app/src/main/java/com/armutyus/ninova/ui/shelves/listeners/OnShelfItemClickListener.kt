package com.armutyus.ninova.ui.shelves.listeners

import com.armutyus.ninova.roomdb.entities.LocalShelf

interface OnShelfItemClickListener {
    fun onClick(localShelf: LocalShelf)
}