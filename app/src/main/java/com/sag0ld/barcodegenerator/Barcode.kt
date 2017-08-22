package com.sag0ld.barcodegenerator

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.common.BitMatrix

/**
 * Created by Sagold on 2017-08-18.
 */
abstract class Barcode {
    abstract var content : String
    abstract fun generate () : Bitmap
    abstract override fun toString () : String

    /**
     * Writes the given Matrix on a new Bitmap object.
     * @param matrix the matrix to write.
     * @return the new {@link Bitmap}-object.
     */
    fun toBitmap(matrix : BitMatrix) : Bitmap {
        val height = matrix.height
        val width = matrix.width
        val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        var x : Int = 0
        while ( x < width - 1) {
            x++
            var y :Int = 0
            while (y < height - 1){
                y++
                bmp.setPixel(x, y, if (matrix.get(x, y)) Color.WHITE else Color.BLACK)
            }
        }
        return bmp
    }
}