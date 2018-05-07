package com.sag0ld.barcodegenerator

import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.oned.Code128Writer

/**
 * Created by Sagold on 2017-08-23.
 */class Code128(override var content: String) : Barcode () {

    init {
        //Validation
        if(content.isEmpty() || content.length > 80)
            throw Exception ("Contents length should be between 1 and 80 characters.")
    }
    override fun generate(): Bitmap {
        val barcode = Code128Writer().encode(content, BarcodeFormat.CODE_128, width,height)
        return toBitmap(barcode)
    }

    override fun toString(): String {
        return "Code 128"
    }
}