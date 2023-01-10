package com.armutyus.ninova.roomdb

import androidx.room.*
import com.armutyus.ninova.model.DataModel
import com.armutyus.ninova.roomdb.entities.BookShelfCrossRef
import com.armutyus.ninova.roomdb.entities.BookWithShelves
import com.armutyus.ninova.roomdb.entities.LocalShelf
import com.armutyus.ninova.roomdb.entities.ShelfWithBooks

@Dao
interface NinovaDao {
    //Book works
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBook(localBook: DataModel.LocalBook)

    @Update
    suspend fun updateBook(localBook: DataModel.LocalBook)

    @Delete
    suspend fun deleteBook(localBook: DataModel.LocalBook)

    @Query("DELETE FROM Book WHERE bookId = :id")
    suspend fun deleteBookById(id: String)

    @Query("SELECT * FROM Book WHERE bookId = :id")
    fun getLocalBooksById(id: String): DataModel.LocalBook

    @Query("SELECT * FROM Book")
    fun getLocalBooks(): List<DataModel.LocalBook>

    @Query("SELECT * FROM Book WHERE bookAuthors LIKE :searchString OR bookTitle LIKE :searchString")
    fun searchLocalBooks(searchString: String): List<DataModel.LocalBook>

    //Shelf works
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShelf(localShelf: LocalShelf)

    @Update
    suspend fun updateShelf(localShelf: LocalShelf)

    @Delete
    suspend fun deleteShelf(localShelf: LocalShelf)

    @Query("SELECT * FROM Shelf")
    fun getLocalShelves(): List<LocalShelf>

    @Query("SELECT * FROM Shelf WHERE shelfTitle LIKE :searchString")
    fun searchLocalShelf(searchString: String): List<LocalShelf>

    //Cross works
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookShelfCrossRef(crossRef: BookShelfCrossRef)

    @Delete
    suspend fun deleteBookShelfCrossRef(crossRef: BookShelfCrossRef)

    @Transaction
    @Query("SELECT * FROM BookShelfCrossRef")
    fun getBookShelfCrossRef(): List<BookShelfCrossRef>

    @Transaction
    @Query("SELECT * FROM Shelf")
    fun getBooksOfShelf(): List<ShelfWithBooks>

    @Transaction
    @Query("SELECT * FROM Book WHERE bookId = :bookId")
    fun getShelvesOfBook(bookId: String): List<BookWithShelves>
}