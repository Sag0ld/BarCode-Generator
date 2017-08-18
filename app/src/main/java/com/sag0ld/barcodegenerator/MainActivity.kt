package com.sag0ld.barcodegenerator

import android.graphics.Bitmap
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Set the barcode as imageView
        val barcodeView : ImageView = findViewById(R.id.barcodeView)

        fun setBarcodeImage (b : Bitmap) {
            barcodeView.setImageBitmap(b)
        }

        // Init the barcode spinner
        val barcodeTypeSpinner : Spinner = findViewById(R.id.barcodeTypeSpinner)
        val adapter : ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(this,
                            R.array.type_of_barcode,android.R.layout.simple_spinner_item)

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        barcodeTypeSpinner.adapter = adapter

        barcodeTypeSpinner.setOnItemClickListener { adapterView, view, i, l ->
            setBarcodeImage(Controller.instance.generateBarcode(barcodeTypeSpinner.selectedItem.toString() ,
                    contentEditText.text.toString()))
        }
        // Init the content editText
        val contentEditText : EditText = findViewById(R.id.contentEditText)

        contentEditText.addTextChangedListener( object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                setBarcodeImage(Controller.instance.generateBarcode(barcodeTypeSpinner.selectedItem.toString() ,
                        p0.toString()))
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })
    }
}