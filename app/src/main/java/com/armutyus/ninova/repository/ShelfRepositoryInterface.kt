package com.armutyus.ninova.repository

import com.armutyus.ninova.roomdb.LocalShelf
import kotlinx.coroutines.flow.Flow

interface ShelfRepositoryInterface {

    suspend fun insert(localShelf: LocalShelf)

    suspend fun update(localShelf: LocalShelf)

    suspend fun delete(localShelf: LocalShelf)

    fun getLocalShelves(): Flow<List<LocalShelf>>

    fun searchLocalShelves(searchString: String): Flow<List<LocalShelf>>

}