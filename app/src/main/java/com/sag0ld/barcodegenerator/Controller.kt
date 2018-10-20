package com.sag0ld.barcodegenerator

import android.graphics.Bitmap
import com.sag0ld.barcodegenerator.barcodes.*
import java.util.*

class Controller private constructor() {
    private object Holder { val INSTANCE = Controller() }
    private var barcode : AbstractBarcode? = null
    private var createAt: Calendar? = null

    companion object {
        val instance : Controller by lazy { Holder.INSTANCE }
    }

    fun createBarcodeEntity(type: String): AbstractBarcode? {
        createAt = Calendar.getInstance()
        barcode = when (type) {
            "UPC-A"     -> UPCA(null, createAt)
            "UPC-E"     -> UPCE(null, createAt)
            "EAN-8"     -> EAN8(null, createAt)
            "EAN-13"    -> EAN13(null, createAt)
            "Code 128"  -> Code128(null, createAt)
            "QR Code"   -> QRCode(null, createAt)
            else -> UPCA(null, createAt)
        }
        return barcode
    }

    fun generateBitmap (type: String, content : String) : Bitmap? {
        if (barcode == null)
            createBarcodeEntity(type)
        barcode?.content = content
        return barcode?.generate()
    }

    fun getBarcodeDescription(): String? {
        return barcode?.description
    }

    fun getBarcode(): AbstractBarcode? {
        return barcode
    }
}