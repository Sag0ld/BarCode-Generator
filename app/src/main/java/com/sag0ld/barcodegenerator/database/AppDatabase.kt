package com.sag0ld.barcodegenerator.database

import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.Database

@Database(entities = arrayOf(Barcode::class), version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun userDao(): BarcodeDao
}