package com.armutyus.ninova.repository

import com.armutyus.ninova.roomdb.NinovaDao
import com.armutyus.ninova.roomdb.entities.BookShelfCrossRef
import com.armutyus.ninova.roomdb.entities.LocalShelf
import com.armutyus.ninova.roomdb.entities.ShelfWithBooks
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ShelfRepository @Inject constructor(
    private val ninovaDao: NinovaDao
) : ShelfRepositoryInterface {
    override suspend fun insert(localShelf: LocalShelf) {
        ninovaDao.insertShelf(localShelf)
    }

    override suspend fun update(localShelf: LocalShelf) {
        ninovaDao.updateShelf(localShelf)
    }

    override suspend fun delete(localShelf: LocalShelf) {
        ninovaDao.deleteShelf(localShelf)
    }

    override fun getLocalShelves(): Flow<List<LocalShelf>> {
        return ninovaDao.getLocalShelves()
    }

    override fun searchLocalShelves(searchString: String): Flow<List<LocalShelf>> {
        return ninovaDao.searchLocalShelf(searchString)
    }

    override suspend fun insertBookShelfCrossRef(crossRef: BookShelfCrossRef) {
        ninovaDao.insertBookShelfCrossRef(crossRef)
    }

    override suspend fun deleteBookShelfCrossRef(crossRef: BookShelfCrossRef) {
        ninovaDao.deleteBookShelfCrossRef(crossRef)
    }

    override suspend fun getShelfWithBooks(shelfId: Int): Flow<List<ShelfWithBooks>> {
        return ninovaDao.getBooksOfShelf(shelfId)
    }
}