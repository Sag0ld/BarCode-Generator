package com.sag0ld.barcodegenerator

import android.graphics.Bitmap

/**
 * Created by Sagold on 2017-08-17.
 */
class Controller private constructor(){
    private object Holder { val INSTANCE = Controller()}

    companion object {
        val instance : Controller by lazy {Holder.INSTANCE}
    }

    fun generateBarcode (type : String, content : String) : Bitmap {
        val barcode :Barcode
        try {
            when (type) {
                "UPC-A" -> barcode = UPCA(content)
                "UPC-E" -> barcode = UPCE(content)
                else -> barcode = UPCA(content)
            }
       } catch ( e : Exception) {
            throw Exception(e)
        }
        return barcode.generate()
    }
}