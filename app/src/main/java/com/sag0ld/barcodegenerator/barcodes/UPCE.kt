package com.sag0ld.barcodegenerator

import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.oned.UPCEWriter
import com.sag0ld.barcodegenerator.barcodes.Barcode
import java.util.*

class UPCE(override var content: String?, override var createAt: Calendar?) : Barcode() {

    override var description: String = App.getContext().getString(R.string.upce_description)

    override fun generate(): Bitmap {
        val barcode = UPCEWriter().encode(content, BarcodeFormat.UPC_E, width, height)
        return toBitmap(barcode)
    }

    override fun toString(): String {
        return "UPC-E"
    }
}