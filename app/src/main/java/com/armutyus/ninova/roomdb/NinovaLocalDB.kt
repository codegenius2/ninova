package com.armutyus.ninova.roomdb

import androidx.room.Database
import androidx.room.RoomDatabase
import com.armutyus.ninova.roomdb.entities.BookShelfCrossRef
import com.armutyus.ninova.roomdb.entities.LocalBook
import com.armutyus.ninova.roomdb.entities.LocalShelf

@Database(
    entities = [
        LocalBook::class,
        LocalShelf::class,
        BookShelfCrossRef::class
    ], version = 1, exportSchema = false
)
abstract class NinovaLocalDB : RoomDatabase() {
    abstract fun ninovaDao(): NinovaDao
}