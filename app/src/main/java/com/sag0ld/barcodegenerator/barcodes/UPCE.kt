package com.sag0ld.barcodegenerator

import android.content.res.Resources
import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.oned.UPCEWriter

class UPCE(override var content: String?) : Barcode() {

    override var description: String = App.getContext().getString(R.string.upce_description)

    override fun generate(): Bitmap {
        val barcode = UPCEWriter().encode(content, BarcodeFormat.UPC_E, width, height)
        return toBitmap(barcode)
    }

    override fun toString(): String {
        return "UPC-E"
    }
}