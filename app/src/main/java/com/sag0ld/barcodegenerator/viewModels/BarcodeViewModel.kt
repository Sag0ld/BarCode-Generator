package com.sag0ld.barcodegenerator.viewModels

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.LiveData
import com.sag0ld.barcodegenerator.barcodes.AbstractBarcode
import com.sag0ld.barcodegenerator.database.Barcode
import com.sag0ld.barcodegenerator.database.RoomRepository
import com.sag0ld.barcodegenerator.database.StaticRepository


class BarcodeViewModel: ViewModel() {

    private var barcodes: LiveData<ArrayList<Barcode>> = MutableLiveData()

    private val repository =  RoomRepository()
    fun getBarcodes(): LiveData<List<Barcode>> {
        return repository.getAll()
    }

    fun addBarcode(barcode: Barcode) {
        //barcodes.value?.add(barcode)
        repository.insert(barcode)
    }

    private fun loadBarcodes() {

    }
}