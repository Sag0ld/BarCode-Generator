package com.sag0ld.barcodegenerator

import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.oned.EAN8Writer

/**
 * Created by Sagold on 2017-08-22.
 */
class EAN8 (override var content: String) : Barcode() {

    init {
        //Validation
        if (content.length > 8) {
            throw Exception ("Must be 7 or 8 digit long.")
        }
    }

    override fun generate(): Bitmap {
        val barcode = EAN8Writer().encode(content, BarcodeFormat.EAN_8, width, height)
        return toBitmap(barcode)
    }

    override fun toString(): String {
        return "EAN-8"
    }

}