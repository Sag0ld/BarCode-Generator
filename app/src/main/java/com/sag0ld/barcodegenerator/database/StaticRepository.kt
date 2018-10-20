package com.sag0ld.barcodegenerator.database

import android.arch.lifecycle.MutableLiveData
import com.sag0ld.barcodegenerator.barcodes.Code128
import com.sag0ld.barcodegenerator.barcodes.QRCode
import com.sag0ld.barcodegenerator.barcodes.AbstractBarcode
import java.util.*
import kotlin.collections.ArrayList

class StaticRepository: dataInterface {

    private var mutableLiveData = MutableLiveData<ArrayList<AbstractBarcode>>()

    override fun getAll(): MutableLiveData<ArrayList<AbstractBarcode>> {
        val barcode1 = Code128("Hola", Calendar.getInstance())
        val barcode2 = Code128("hsbdj", Calendar.getInstance())
        val barcode3 = QRCode("Hola", Calendar.getInstance())
        val lists = ArrayList<AbstractBarcode>()
        lists.add(barcode1)
        lists.add(barcode2)
        lists.add(barcode3)
        mutableLiveData.value = lists
        return mutableLiveData
    }

    override fun insert(barcode: AbstractBarcode) {
        mutableLiveData.value?.add(barcode)
    }

    override fun delete(barcode: AbstractBarcode) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}