package com.sag0ld.barcodegenerator.barcodes

import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.sag0ld.barcodegenerator.App
import com.sag0ld.barcodegenerator.R
import java.util.*

class QRCode(override var content: String?, override var createAt: Calendar?) : AbstractBarcode() {
    override val maxLength = 9999
    override var description: String = App.instance.applicationContext.getString(R.string.qr_description)

    override fun isValid(content: String): Boolean {
        return true
    }

    override fun generate(): Bitmap? {
       val barcode = QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, width, height*3)
        return toBitmap(barcode)
    }

    override fun toString(): String {
       return "QR Code"
    }
}