package com.sag0ld.barcodegenerator

import android.graphics.Bitmap

class Controller private constructor(){
    private object Holder { val INSTANCE = Controller()}
    private var barcode :Barcode? = null

    companion object {
        val instance : Controller by lazy {Holder.INSTANCE}
    }

    fun createBarcodeBox(type: String) {
        barcode = when (type) {
            "UPC-A"     -> UPCA(null)
            "UPC-E"     -> UPCE(null)
            "EAN-8"     -> EAN8(null)
            "EAN-13"    -> EAN13(null)
            "Code 128"  -> Code128(null)
            "QR Code"   -> QRCode(null)
            else -> UPCA(null)
        }
    }

    fun generateBarcode (type: String, content : String) : Bitmap? {
        if (barcode == null)
            createBarcodeBox(type)
        barcode?.content = content
        return barcode?.generate()
    }

    fun getBarcodeDescription(): String? {
        return barcode?.description
    }
}