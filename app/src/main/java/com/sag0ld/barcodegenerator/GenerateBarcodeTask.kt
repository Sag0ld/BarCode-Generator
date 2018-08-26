package com.sag0ld.barcodegenerator

import android.graphics.Bitmap
import android.os.AsyncTask
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import java.lang.reflect.InvocationTargetException

class GenerateBarcodeTask (private val holder : RelativeLayout, private val barcodeView : ImageView)
    : AsyncTask<String, Int, Unit>() {

    private var mException: Exception? = null
    private lateinit var mBitmap: Bitmap

    override fun onPreExecute() {
        super.onPreExecute()
        // Show progressBar
        holder.visibility = View.VISIBLE
    }

    override fun onPostExecute(result: Unit) {
        if (mException != null) {
            throw mException!!
        } else {
            // Hide progressBar
            holder.visibility = View.GONE
            barcodeView.setImageBitmap(mBitmap)
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