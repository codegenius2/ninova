package com.armutyus.ninova.roomdb

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [LocalBook::class, LocalShelf::class], version = 1, exportSchema = false)
abstract class NinovaLocalDB : RoomDatabase() {
    abstract fun ninovaDao(): NinovaDao
}