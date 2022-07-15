package com.armutyus.ninova.roomdb

import androidx.room.*
import com.armutyus.ninova.roomdb.entities.*
import kotlinx.coroutines.flow.Flow

@Dao
interface NinovaDao {
    //Book works

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(localBook: LocalBook)

    @Update
    suspend fun updateBook(localBook: LocalBook)

    @Delete
    suspend fun deleteBook(localBook: LocalBook)

    @Query("SELECT * FROM Book WHERE bookId = :id")
    fun getLocalBooksById(id: Int): Flow<LocalBook>

    @Query("SELECT * FROM Book")
    fun getLocalBooks(): Flow<List<LocalBook>>

    @Query("SELECT * FROM Book WHERE bookAuthors LIKE :searchString OR bookTitle LIKE :searchString")
    fun searchLocalBooks(searchString: String): Flow<List<LocalBook>>

    //Shelf works

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShelf(localShelf: LocalShelf)

    @Update
    suspend fun updateShelf(localShelf: LocalShelf)

    @Delete
    suspend fun deleteShelf(localShelf: LocalShelf)

    @Query("SELECT * FROM Shelf")
    fun getLocalShelves(): Flow<List<LocalShelf>>

    @Query("SELECT * FROM Shelf WHERE shelfTitle LIKE :searchString")
    fun searchLocalShelf(searchString: String): Flow<List<LocalShelf>>

    //Cross works

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookShelfCrossRef(crossRef: BookShelfCrossRef)

    @Delete
    suspend fun deleteBookShelfCrossRef(crossRef: BookShelfCrossRef)

    @Transaction
    @Query("SELECT * FROM Shelf")
    fun getBooksOfShelf(): Flow<List<ShelfWithBooks>>

    @Transaction
    @Query("SELECT * FROM Book WHERE bookId = :bookId")
    fun getShelvesOfBook(bookId: Int): Flow<List<BookWithShelves>>
}