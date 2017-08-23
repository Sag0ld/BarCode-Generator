package com.sag0ld.barcodegenerator

import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.oned.UPCEWriter

/**
 * Created by Sagold on 2017-08-22.
 */
class UPCE(override var content: String) : Barcode() {

    init {
        //Validation
        if (content.length > 7) {
            throw Exception ("Must be 7 digit long.")
        }
    }

    override fun generate(): Bitmap {
        val barcode = UPCEWriter().encode(content, BarcodeFormat.UPC_E, width, height)
        return toBitmap(barcode)
    }

    override fun toString(): String {
        return "UPC-E"
    }
}