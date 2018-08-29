package com.sag0ld.barcodegenerator

import android.content.res.Resources
import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.oned.UPCAWriter

/**
 * Created by Sagold on 2017-08-18.
 */
class UPCA (override var content: String?) : Barcode() {

    override var description: String = App.getContext().getString(R.string.upca_description)

    override fun generate(): Bitmap {
        var barcode = UPCAWriter().encode(content, BarcodeFormat.UPC_A
                                                     ,width,height)
        return toBitmap(barcode)
    }

    override fun toString(): String {
        return "UPC-A"
    }

}