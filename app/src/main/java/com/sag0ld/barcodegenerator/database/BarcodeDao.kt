package com.sag0ld.barcodegenerator.database

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query

@Dao
interface BarcodeDao {
    @Query("SELECT * FROM barcodes")
    fun getAll(): LiveData<List<Barcode>>

    @Insert
    fun insert(barcode: Barcode)

    @Delete
    fun delete(barcode: Barcode)
}