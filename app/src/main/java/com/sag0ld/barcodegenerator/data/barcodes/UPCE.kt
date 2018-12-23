package com.sag0ld.barcodegenerator.data.barcodes

import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.oned.UPCEWriter
import com.sag0ld.barcodegenerator.data.base.App
import com.sag0ld.barcodegenerator.R
import java.util.*

class UPCE(override var content: String, override var createAt: Calendar?) : AbstractBarcode() {

    companion object {
    }

    override val maxLength = 7
    override var description: String = App.instance.applicationContext.getString(R.string.upce_description)

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

        if (content[0] != '0' && content[0] != '1') {
            errors.appendln("The content must begin with 0 or 1")
            return false
        }

        return true
    }

    override fun generate(): Bitmap {
        val barcode = UPCEWriter().encode(content, BarcodeFormat.UPC_E, width, height)
        return toBitmap(barcode)
    }

    override fun toString(): String {
        return "UPC-E"
    }
}