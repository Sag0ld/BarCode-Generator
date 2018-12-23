package com.sag0ld.barcodegenerator.data

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import com.sag0ld.barcodegenerator.domain.Barcode

@Dao
interface BarcodeDao {
    @Query("SELECT * FROM barcodes")
    fun getAll(): LiveData<List<Barcode>>

    @Query("SELECT * FROM barcodes WHERE id = :barcodeId")
    fun getBarcodeById(barcodeId: Long): LiveData<Barcode>

    @Insert
    fun insert(barcode: Barcode)

    @Delete
    fun delete(barcode: Barcode)
}