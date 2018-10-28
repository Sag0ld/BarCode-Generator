package com.sag0ld.barcodegenerator

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.sag0ld.barcodegenerator.barcodes.AbstractBarcode
import com.sag0ld.barcodegenerator.database.Barcode
import com.sag0ld.barcodegenerator.viewModels.BarcodeViewModel
import com.sag0ld.barcodegenerator.viewModels.CodeDetailsViewModel

import kotlinx.android.synthetic.main.content_code_details.*
import org.jetbrains.anko.doAsync
import java.util.*

class CodeDetailsActivity : AppCompatActivity() {

    companion object {
        val EXTRA_ID = "ID"
    }

    private lateinit var model : CodeDetailsViewModel
    private var codeDetails: AbstractBarcode? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_code_details)

        val barcodeID = intent.getLongExtra(EXTRA_ID, 0L)
        model = ViewModelProviders.of(this).get(CodeDetailsViewModel::class.java)

        model.barcodeLiveData = model.getBarcode(barcodeID)
        model.barcodeLiveData.observe(this, Observer<Barcode> {
            it?.let { barcode ->
                barcode.type?.let { type ->
                    codeDetails = Controller.instance.createBarcodeEntity(type)
                }

                codeDetails?.let { code ->
                    code.content = barcode.content
                    code.createAt = Calendar.getInstance()
                    barcode.createAt?.let { lastUpdateInMillis ->
                        code.createAt?.timeInMillis = lastUpdateInMillis
                    }
                    doAsync {
                        model.bitmapLiveData.postValue(code.generate())
                    }
                }

            }
        })
    }

    override fun onResume() {
        super.onResume()
        model.bitmapLiveData.observe(this, Observer<Bitmap> { it?.let { bitmap ->
            Glide.with(this)
                    .load(bitmap)
                    .into(codeImageViewDetails)
        } })

        model.barcodeLiveData.observe(this, Observer<Barcode> { it?.let {barcode ->
            codeTypeTextView?.text = barcode.type
            codeContentTextView?.text = barcode.content
            codeDateTextView?.text = barcode.createAtDatetoString()
        }})
    }
}
