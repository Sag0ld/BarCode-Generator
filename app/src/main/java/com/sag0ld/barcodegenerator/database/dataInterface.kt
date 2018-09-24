package com.sag0ld.barcodegenerator.database

import android.arch.lifecycle.MutableLiveData
import com.sag0ld.barcodegenerator.barcodes.AbstractBarcode

interface dataInterface {
    fun getAll(): MutableLiveData<ArrayList<AbstractBarcode>>

    fun insert(barcode: AbstractBarcode)

    fun delete(barcode: AbstractBarcode)
}