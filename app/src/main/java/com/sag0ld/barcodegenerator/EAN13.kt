package com.sag0ld.barcodegenerator

import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.oned.EAN13Writer

/**
 * Created by Sagold on 2017-08-23.
 */
class EAN13(override var content: String) : Barcode() {
    override fun generate(): Bitmap {
        val barcode = EAN13Writer().encode(content, BarcodeFormat.EAN_13, width,height)
        return toBitmap(barcode)
    }

    override fun toString(): String {
       return  "EAN-13"
    }
}