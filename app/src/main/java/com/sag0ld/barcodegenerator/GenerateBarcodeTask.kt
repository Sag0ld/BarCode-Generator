package com.sag0ld.barcodegenerator

import android.graphics.Bitmap
import android.os.AsyncTask
import android.view.View
import android.widget.FrameLayout
import java.lang.reflect.InvocationTargetException

class GenerateBarcodeTask (private val holder : FrameLayout)
    : AsyncTask<String, Int, Unit>() {

    companion object {
        var listener: AsyncResponse? = null
    }

    private var mException: Exception? = null
    private lateinit var mBitmap: Bitmap


    override fun onPreExecute() {
        super.onPreExecute()
        holder.visibility = View.VISIBLE
    }

    override fun onPostExecute(result: Unit) {
        if (mException != null) {
            throw mException!!
        } else {
            // Hide progressBar
            holder.visibility = View.GONE
            listener?.processFinish(mBitmap)
        }
    }

    override fun doInBackground(vararg args: String) {
        try {
            Controller.instance.generateBarcode(args[0], args[1])?.let {
                mBitmap = it
            }
        } catch (exception: InvocationTargetException) {
            mException = exception
        } catch (exception: Exception) {
            mException = exception
        }
    }
}

interface AsyncResponse {
    fun processFinish(output: Bitmap)
}