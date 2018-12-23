package com.sag0ld.barcodegenerator.domain.barcodes

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.common.BitMatrix
import com.sag0ld.barcodegenerator.ui.base.App
import com.sag0ld.barcodegenerator.R
import java.util.*

abstract class AbstractBarcode {

    val width = 160 * 5
    val height = 101 * 3
    abstract val maxLength: Int

    var errors = StringBuilder()
    abstract var content : String
    abstract var description : String
    abstract var createAt : Calendar?

    abstract fun generate () : Bitmap?
    abstract fun isValid(): Boolean
    abstract override fun toString () : String

    open fun hasDelayToGenerateBitmap():Boolean {
        return false
    }

    fun getErrorsMessage(): String {
        val tmpErrors = errors.toString()
        errors.delete(0, errors.length)
        return tmpErrors
    }

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
                bmp.setPixel(x,y,if (matrix.get(x, y)) App.instance.resources.getColor(R.color.brown) else Color.TRANSPARENT)
            }
        }

        return bmp
    }
}