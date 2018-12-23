package com.sag0ld.barcodegenerator.data.barcodes

import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.oned.EAN13Writer
import com.sag0ld.barcodegenerator.data.base.App
import com.sag0ld.barcodegenerator.R
import java.util.*

class EAN13(override var content: String, override var createAt: Calendar?) : AbstractBarcode() {

    companion object {
        val TYPE = "EAN-13"
    }

    override val maxLength = 12
    override var description: String = App.instance.applicationContext.getString(R.string.ean13_description)

    override fun isValid(): Boolean {
        if (content.length < maxLength)
            return false
        if (content.length > maxLength) {
            errors.appendln("The content must be $maxLength digit long.")
            return false
        }
        if (content.length == maxLength && !content.matches(Regex("^\\d+$"))) {
            errors.appendln("The content must be only digit.")
            return false
        }
        return true
    }

    override fun generate(): Bitmap {
        val barcode = EAN13Writer().encode(content, BarcodeFormat.EAN_13, width,height)
        return toBitmap(barcode)
    }

    override fun toString(): String {
       return  "EAN-13"
    }
}