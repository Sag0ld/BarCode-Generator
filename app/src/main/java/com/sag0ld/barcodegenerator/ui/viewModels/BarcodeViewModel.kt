package com.sag0ld.barcodegenerator.ui.viewModels

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.LiveData
import android.graphics.Bitmap
import com.sag0ld.barcodegenerator.domain.barcodes.AbstractBarcode
import com.sag0ld.barcodegenerator.domain.models.Code
import com.sag0ld.barcodegenerator.data.RoomRepository


class BarcodeViewModel: ViewModel() {

    private val repository =  RoomRepository()
    private var codes: LiveData<ArrayList<Code>> = MutableLiveData()

    var content = ""
    var currentBitmapLiveData = MutableLiveData<Bitmap>()
    var currentBarcodeLiveData = MutableLiveData<AbstractBarcode>()

    fun getCodes(): LiveData<List<Code>> {
        return repository.getAll()
    }

    fun addCode(code: Code) {
        repository.insert(code)
    }

    fun deleteCode(code: Code) {
        repository.delete(code)
    }
}