package com.sag0ld.barcodegenerator.ui.viewModels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.graphics.Bitmap
import com.sag0ld.barcodegenerator.domain.Barcode
import com.sag0ld.barcodegenerator.data.RoomRepository

class CodeDetailsViewModel: ViewModel() {

    private val repository =  RoomRepository()
    var bitmapLiveData = MutableLiveData<Bitmap>()
    lateinit var barcodeLiveData: LiveData<Barcode>

    fun getBarcode(barcodeId: Long): LiveData<Barcode> {
        return repository.getBarcodeById(barcodeId)
    }

    fun deleteBarcode(barcode: Barcode) {
        repository.delete(barcode)
    }
}