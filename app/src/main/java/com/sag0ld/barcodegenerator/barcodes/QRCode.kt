package com.sag0ld.barcodegenerator

import android.content.res.Resources
import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter

class QRCode(override var content: String?) : Barcode() {

    override var description: String = App.getContext().getString(R.string.qr_description)

    override fun generate(): Bitmap {
       val barcode = QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, width, height*3)
        return toBitmap(barcode)
    }

    override fun toString(): String {
       return "QR Code"
    }
}