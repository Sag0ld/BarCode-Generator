package com.sag0ld.barcodegenerator

import android.graphics.Bitmap
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.sag0ld.barcodegenerator.barcodes.Barcode
import java.util.*

class QRCode(override var content: String?, override var createAt: Calendar?) : Barcode() {
    override var description: String = App.getContext().getString(R.string.qr_description)

    override fun generate(): Bitmap {
       val barcode = QRCodeWriter().encode(content, BarcodeFormat.QR_CODE, width, height*3)
        return toBitmap(barcode)
    }

    override fun toString(): String {
       return "QR Code"
    }
}