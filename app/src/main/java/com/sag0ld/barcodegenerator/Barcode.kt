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

        val bmp = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)

        for (x in 0 .. (width - 1)) {
            for(y in 0 .. (height - 1)) {
                bmp.setPixel(x,y,if (matrix.get(x, y)) Color.BLACK else Color.WHITE)
            }
        }

        return bmp
    }
}