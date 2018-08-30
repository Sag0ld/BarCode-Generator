package com.sag0ld.barcodegenerator

import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.oned.UPCEWriter
import com.sag0ld.barcodegenerator.barcodes.AbstractBarcode
import java.util.*

class UPCE(override var content: String?, override var createAt: Calendar?) : AbstractBarcode() {

    override var description: String = App.instance.applicationContext.getString(R.string.upce_description)

    override fun generate(): Bitmap {
        val barcode = UPCEWriter().encode(content, BarcodeFormat.UPC_E, width, height)
        return toBitmap(barcode)
    }

    override fun toString(): String {
        return "UPC-E"
    }
}