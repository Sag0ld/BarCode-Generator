package com.sag0ld.barcodegenerator.data

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.*
import com.sag0ld.barcodegenerator.domain.Barcode

@Dao
interface BarcodeDao {
    @Query("SELECT * FROM barcodes")
    fun getAll(): LiveData<List<Barcode>>

    @Query("SELECT * FROM barcodes WHERE id = :barcodeId")
    fun getBarcodeById(barcodeId: Long): LiveData<Barcode>

    @Update
    fun update(barcode: Barcode)

    @Insert
    fun insert(barcode: Barcode)

    @Delete
    fun delete(barcode: Barcode)
}