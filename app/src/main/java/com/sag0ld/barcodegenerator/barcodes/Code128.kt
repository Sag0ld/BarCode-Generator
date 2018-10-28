package com.sag0ld.barcodegenerator.barcodes

import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.oned.Code128Writer
import com.sag0ld.barcodegenerator.App
import com.sag0ld.barcodegenerator.R
import java.util.*

class Code128(override var content: String?, override var createAt: Calendar?) : AbstractBarcode() {
    override val maxLength = 80
    override var description: String = App.instance.applicationContext.getString(R.string.code128_description)

   override fun isValid(content: String): Boolean {
        if (content.isEmpty() || content.length > 80) {
            errors.appendln("Contents length should be between 1 and 80 characters.")
            return false
        }
        return true
    }

   override fun generate(): Bitmap {
        val barcode = Code128Writer().encode(content, BarcodeFormat.CODE_128, width,height)
        return toBitmap(barcode)
   }

    override fun toString(): String {
        return "Code 128"
    }
}