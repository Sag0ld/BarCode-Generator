package com.sag0ld.barcodegenerator.data

import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.content.Context
import com.sag0ld.barcodegenerator.domain.Barcode

@Database(entities = [(Barcode::class)], version = 1)
abstract class AppDatabase: RoomDatabase() {
    abstract fun barcodeDao(): BarcodeDao

    companion object {
        private var INSTANCE: AppDatabase? = null
        private val DATABASE_NAME = "BarcodeGenerator_Database"

        fun getAppDatabase(context: Context): AppDatabase {
            if (INSTANCE == null) {
                INSTANCE = Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, DATABASE_NAME)
                        .build()
            }
            return INSTANCE as AppDatabase
        }
    }

    fun destroyInstance() {
        INSTANCE = null
    }
}