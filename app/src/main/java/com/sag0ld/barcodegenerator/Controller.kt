package com.sag0ld.barcodegenerator

import com.sag0ld.barcodegenerator.barcodes.*
import java.util.*

class Controller private constructor() {
    private object Holder { val INSTANCE = Controller() }
    private var createAt: Calendar? = null

    companion object {
        val instance : Controller by lazy { Holder.INSTANCE }
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

    /* fun generateBitmap (type: String, content : String) : Bitmap? {
        if (barcode == null)
            createBarcodeEntity(type)
        barcode?.content = content
        return barcode?.generate()
    }*/

    /* fun getBarcodeDescription(): String? {
        return barcode?.description
    }*/
}