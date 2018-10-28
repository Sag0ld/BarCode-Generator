package com.sag0ld.barcodegenerator.viewModels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.graphics.Bitmap
import com.sag0ld.barcodegenerator.database.Barcode
import com.sag0ld.barcodegenerator.database.RoomRepository

class CodeDetailsViewModel: ViewModel() {

    private val repository =  RoomRepository()
    var bitmapLiveData = MutableLiveData<Bitmap>()
    lateinit var barcodeLiveData: LiveData<Barcode>

    fun getBarcode(barcodeId: Long): LiveData<Barcode> {
        return repository.getBarcodeById(barcodeId)
    }
}