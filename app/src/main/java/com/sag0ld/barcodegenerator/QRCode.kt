package com.sag0ld.barcodegenerator

import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter

class QRCode(override var content: String) : Barcode() {
    init {
        // Validation
        if (content.isEmpty())
            throw Exception ("Content can't be empty.")

    }
    override fun generate(): Bitmap {
       val barcode = QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, width, height*3)
        return toBitmap(barcode)
    }

    override fun toString(): String {
       return "QR Code"
    }
}