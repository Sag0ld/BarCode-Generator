package com.sag0ld.barcodegenerator

import android.content.res.Resources
import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.oned.Code128Writer

class Code128(override var content: String?) : Barcode () {

    override var description: String = App.getContext().getString(R.string.code128_description)

    override fun generate(): Bitmap {
        val barcode = Code128Writer().encode(content, BarcodeFormat.CODE_128, width,height)
        return toBitmap(barcode)
    }

    override fun toString(): String {
        return "Code 128"
    }
}