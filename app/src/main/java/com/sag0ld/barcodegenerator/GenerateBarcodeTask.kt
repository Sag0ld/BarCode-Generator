package com.sag0ld.barcodegenerator

import android.graphics.Bitmap
import android.os.AsyncTask
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout

class GenerateBarcodeTask (private val holder : RelativeLayout, private val barcodeView : ImageView,
                           private val editText : EditText)
    : AsyncTask<String, Int, Unit>() {

    private var mException: Exception? = null
    private lateinit var mBitmap: Bitmap

    override fun onPreExecute() {
        super.onPreExecute()
        // Show progressBar
        holder.visibility = View.VISIBLE
        editText.isEnabled = false
    }

    override fun onPostExecute(result: Unit) {
        if (mException != null) {
            throw mException!!
        } else {
            // Hide progressBar
            holder.visibility = View.GONE
            barcodeView.setImageBitmap(mBitmap)
            editText.isEnabled = true
        }
    }

    override fun doInBackground(vararg args: String) {
        try {
            mBitmap = Controller.instance.generateBarcode(args[0], args[1])
        } catch (exception: Exception) {
            mException = exception
        }
    }
}