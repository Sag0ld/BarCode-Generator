package com.sag0ld.barcodegenerator.util

import com.sag0ld.barcodegenerator.data.barcodes.*
import java.util.*

class BarcodeFactory private constructor() {
    private object Holder { val INSTANCE = BarcodeFactory() }
    private var createAt: Calendar? = null

    companion object {
        val instance : BarcodeFactory by lazy { Holder.INSTANCE }
    }

    fun createBarcodeEntity(type: String): AbstractBarcode {
        createAt = Calendar.getInstance()
        return when (type) {
            "UPC-A"     -> UPCA("", createAt)
            "UPC-E"     -> UPCE("", createAt)
            "EAN-8"     -> EAN8("", createAt)
            "EAN-13"    -> EAN13("", createAt)
            "Code 128"  -> Code128("", createAt)
            "QR Code"   -> QRCode("", createAt)
            else -> UPCA("", createAt)
        }
    }
}