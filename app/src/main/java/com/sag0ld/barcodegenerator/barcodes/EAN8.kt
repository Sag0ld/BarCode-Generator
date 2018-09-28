package com.sag0ld.barcodegenerator

import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.oned.EAN8Writer
import com.sag0ld.barcodegenerator.barcodes.AbstractBarcode
import java.util.*

class EAN8 (override var content: String?, override var createAt: Calendar?) : AbstractBarcode() {

    companion object {
        val TYPE = "EAN-8"
    }

    override var description: String = App.instance.applicationContext.getString(R.string.ean8_description)

    override fun generate(): Bitmap {
        val barcode = EAN8Writer().encode(content, BarcodeFormat.EAN_8, width, height)
        return toBitmap(barcode)
    }

    override fun toString(): String {
        return "EAN-8"
    }

}