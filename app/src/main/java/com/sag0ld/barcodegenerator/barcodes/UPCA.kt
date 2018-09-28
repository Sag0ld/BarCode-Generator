package com.sag0ld.barcodegenerator

import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.oned.UPCAWriter
import com.sag0ld.barcodegenerator.barcodes.AbstractBarcode
import java.util.*

class UPCA (override var content: String?, override var createAt: Calendar?) : AbstractBarcode() {

    companion object {
        val TYPE = "UPC-A"
    }

    override var description: String = App.instance.applicationContext.getString(R.string.upca_description)

    override fun generate(): Bitmap {
        var barcode = UPCAWriter().encode(content, BarcodeFormat.UPC_A
                                                     ,width,height)
        return toBitmap(barcode)
    }

    override fun toString(): String {
        return "UPC-A"
    }

}