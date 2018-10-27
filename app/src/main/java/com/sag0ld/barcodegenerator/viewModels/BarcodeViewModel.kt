package com.sag0ld.barcodegenerator.viewModels

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.LiveData
import android.graphics.Bitmap
import com.sag0ld.barcodegenerator.barcodes.AbstractBarcode
import com.sag0ld.barcodegenerator.database.Barcode
import com.sag0ld.barcodegenerator.database.RoomRepository


class BarcodeViewModel: ViewModel() {

    private val repository =  RoomRepository()
    private var barcodes: LiveData<ArrayList<Barcode>> = MutableLiveData()

    var content = ""
    var currentBitmapLiveData = MutableLiveData<Bitmap>()
    var currentBarcodeLiveData = MutableLiveData<AbstractBarcode>()

    fun getBarcodes(): LiveData<List<Barcode>> {
        return repository.getAll()
    }

    fun addBarcode(barcode: Barcode) {
        repository.insert(barcode)
    }

    fun deleteBarcode(barcode: Barcode) {
        repository.delete(barcode)
    }
}