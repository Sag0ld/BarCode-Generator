package com.sag0ld.barcodegenerator

import android.graphics.Bitmap

/**
 * Created by Sagold on 2017-08-17.
 */
class Controller private constructor(){
    private object Holder { val INSTANCE = Controller()}

    companion object {
        val instance : Controller by lazy {Holder.INSTANCE}
    }

    fun generateBarcode (type : String, content : String) : Bitmap {
        val barcode :Barcode
        try {
            when (type) {
                "UPC-A"     -> barcode = UPCA(content)
                "UPC-E"     -> barcode = UPCE(content)
                "EAN-8"     -> barcode = EAN8(content)
                "EAN-13"    -> barcode = EAN13(content)
                "Code 128"  -> barcode = Code128(content)
                "QR Code"   -> barcode = QRCode(content)
                else -> barcode = UPCA(content)
            }
       } catch ( e : Exception) {
            throw Exception(e)
        }
        return barcode.generate()
    }
}