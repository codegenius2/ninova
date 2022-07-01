package com.armutyus.ninova.repository

import com.armutyus.ninova.roomdb.entities.BookShelfCrossRef
import com.armutyus.ninova.roomdb.entities.LocalShelf
import com.armutyus.ninova.roomdb.entities.ShelfWithBooks
import kotlinx.coroutines.flow.Flow

interface ShelfRepositoryInterface {

    suspend fun insert(localShelf: LocalShelf)

    suspend fun update(localShelf: LocalShelf)

    suspend fun delete(localShelf: LocalShelf)

    fun getLocalShelves(): Flow<List<LocalShelf>>

    fun searchLocalShelves(searchString: String): Flow<List<LocalShelf>>

    suspend fun insertBookShelfCrossRef(crossRef: BookShelfCrossRef)

    suspend fun deleteBookShelfCrossRef(crossRef: BookShelfCrossRef)

    suspend fun getShelfWithBooks(shelfId: Int): Flow<List<ShelfWithBooks>>

}