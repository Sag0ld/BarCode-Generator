package com.sag0ld.barcodegenerator.barcodes

import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.oned.UPCAWriter
import com.sag0ld.barcodegenerator.App
import com.sag0ld.barcodegenerator.R
import java.util.*

class UPCA (override var content: String, override var createAt: Calendar?) : AbstractBarcode() {

    companion object {
        val TYPE = "UPC-A"
    }

    override val maxLength = 11
    override var description: String = App.instance.applicationContext.getString(R.string.upca_description)

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
        var barcode = UPCAWriter().encode(content, BarcodeFormat.UPC_A
                                                     ,width,height)
        return toBitmap(barcode)
    }

    override fun toString(): String {
        return "UPC-A"
    }

}