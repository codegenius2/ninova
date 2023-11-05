package com.armutyus.ninova.roomdb

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.armutyus.ninova.model.googlebooksmodel.DataModel
import com.armutyus.ninova.roomdb.entities.BookShelfCrossRef
import com.armutyus.ninova.roomdb.entities.LocalShelf

@Database(
    entities = [
        DataModel.LocalBook::class,
        LocalShelf::class,
        BookShelfCrossRef::class
    ], version = 1, exportSchema = true
)
@TypeConverters(DataConverter::class)
abstract class NinovaLocalDB : RoomDatabase() {
    abstract fun ninovaDao(): NinovaDao
}