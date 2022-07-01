package com.armutyus.ninova.repository

import com.armutyus.ninova.roomdb.LocalShelf
import com.armutyus.ninova.roomdb.NinovaDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ShelfRepository @Inject constructor(
    private val ninovaDao: NinovaDao
): ShelfRepositoryInterface {
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
}