package com.sag0ld.barcodegenerator

import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.TextInputLayout
import android.text.*
import android.text.style.ForegroundColorSpan
import android.view.View
import android.widget.*
import kotlinx.android.synthetic.main.activity_main.*
import android.text.InputFilter
import android.os.Handler

class MainActivity : AppCompatActivity() {
    private val errorsMessages = StringBuilder()
    lateinit var contentEditText : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Init val
        val contentTextLayout : TextInputLayout = findViewById(R.id.contentTextInputLayout)
        contentEditText = contentTextLayout.editText!!
        val barcodeTypeSpinner : Spinner = findViewById(R.id.barcodeTypeSpinner)
        val barcodeView  : ImageView = findViewById(R.id.barcodeView)
        var progressBarHolder : RelativeLayout = findViewById(R.id.progressBarHolder)

        // Init the barcode spinner
        val adapter : ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(this,
                            R.array.type_of_barcode,android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        barcodeTypeSpinner.adapter = adapter

        // Barcode spinner on child selected
        barcodeTypeSpinner.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) { }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val type = barcodeTypeSpinner.selectedItem.toString()

                // Trim the content of the editText if is longer
                val maxLength = getMaxLength()
                if(contentEditText.length() > maxLength)
                    contentEditText.text.delete(maxLength, contentEditText.length())

                // Set limit input
                val FilterArray = arrayOfNulls<InputFilter>(1)
                FilterArray[0] = InputFilter.LengthFilter(maxLength)
                contentEditText.filters = FilterArray

                // Update the limit of editText
                updateCounterMessage(contentEditText)

                // Update the inputType of the keyboard
                when (type) {
                    "UPC-A", "UPC-E", "EAN-8", "EAN-13" ->  contentEditText.inputType =
                                                                InputType.TYPE_CLASS_NUMBER
                    else -> contentEditText.inputType = InputType.TYPE_CLASS_TEXT
                }

                val content = contentEditText.text.toString()

                // Generate a barcode
                if (isValid(type, content)) {
                    try {
                        GenerateBarcodeTask(progressBarHolder, barcodeView, contentEditText)
                                .execute(type, p0.toString())
                    } catch (e: Exception) {
                        // Show errors message from API
                        Toast.makeText(this@MainActivity, e.toString(),
                                        Toast.LENGTH_SHORT).show()
                    }
                } else if (errorsMessages.isNotEmpty()) {
                    // Show errors messages from the front end
                    Toast.makeText(this@MainActivity, errorsMessages.toString(),
                                    Toast.LENGTH_SHORT).show()
                    // Erase messages
                    errorsMessages.delete(0, errorsMessages.lastIndex + 1)
                }
            }
        }

        // Init the content editText event
        contentEditText.addTextChangedListener( object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (!p0.isNullOrEmpty()) {
                    updateCounterMessage(contentEditText)

                    val type = barcodeTypeSpinner.selectedItem.toString()
                    if(type == "Code 128" || type == "QR Code") {
                        last_text_edit = System.currentTimeMillis()
                       try {
                           handler.postDelayed(input_finish_checker, delay)
                       }
                       catch (e: Exception) {
                           // Show errors message from API
                           Toast.makeText(this@MainActivity, e.message,
                                   Toast.LENGTH_SHORT).show()
                       }
                    }
                }
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val type = barcodeTypeSpinner.selectedItem.toString()

                // Generate a barcode and set the imageView
                if (isValid(type, p0.toString())) {
                   try {
                       if (type == "Code 128" || type == "QR Code") {
                           handler.removeCallbacks(input_finish_checker)
                       }
                       else {
                           GenerateBarcodeTask(progressBarHolder, barcodeView, contentEditText)
                                   .execute(type, p0.toString())
                       }
                   } catch (e: Exception) {
                       // Show errors message from API
                       Toast.makeText(this@MainActivity, e.message,
                                        Toast.LENGTH_SHORT).show()
                   }
               } else if (errorsMessages.isNotEmpty()) {
                    // Show errors messages from the front end
                    Toast.makeText(this@MainActivity, errorsMessages.toString(),
                                    Toast.LENGTH_SHORT).show()
                    // Erase messages
                    errorsMessages.delete(0, errorsMessages.lastIndex + 1)
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }
        })
    }

    var delay: Long = 2000 // 2 seconds after user stops typing
    var last_text_edit: Long = 0
    var handler = Handler()

    private val input_finish_checker = Runnable {
        val type = barcodeTypeSpinner.selectedItem.toString()
        if (System.currentTimeMillis() > last_text_edit + delay - 500) {
            if(isValid(type, contentEditText.text.toString())) {
                GenerateBarcodeTask(progressBarHolder, barcodeView, contentEditText)
                        .execute(type, contentEditText.text.toString())
            }
        }
    }


    fun getMaxLength()  : Int {
        val type = barcodeTypeSpinner.selectedItem.toString()
        val maxLength: Int
        when (type) {
            "UPC-A" -> maxLength = 11
            "UPC-E", "EAN-8" -> maxLength = 7
            "EAN-13" -> maxLength = 12
            "Code 128" -> maxLength = 80
            "QR Code" -> maxLength = 9999
            else -> maxLength = 0
        }
        return maxLength
    }

    fun updateCounterMessage (contentEditText : EditText) {
        val counter = SpannableStringBuilder()
        counter.clear()
        counter.clearSpans()

        val length = contentEditText.length()
        if (length > 0) {

            // Set the max length for the barcode type selected
            val maxLength = getMaxLength()

            counter.append("$length/$maxLength")
            if (length < maxLength)
                counter.setSpan(ForegroundColorSpan(Color.GRAY), 0, counter.length,
                        Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
            if (length == maxLength)
                counter.setSpan(ForegroundColorSpan(Color.GREEN), 0, counter.length,
                        Spanned.SPAN_INCLUSIVE_EXCLUSIVE)

            contentTextInputLayout.error = counter
        }
    }

    private fun isUPCValid(content : String, limit : Int) : Boolean {
        if (content.length < limit)
            return false
        if (content.length > limit) {
            errorsMessages.append("The content must be $limit digit long.")
            return false
        }
        if (content.length == limit && !content.matches(Regex("^\\d+$"))) {
            errorsMessages.append("The content must be only digit.")
            return false
        }

        return true
    }
    private fun isEANValid (content :String, limit : Int) : Boolean {
        return isUPCValid(content, limit)
    }

    private fun isCode128Valid(content : String) : Boolean {
        if (content.isEmpty() || content.length > 80) {
            errorsMessages.append("Contents length should be between 1 and 80 characters.")
            return false
        }
        return true
    }

    private fun isValid (type : String, content : String) : Boolean {
        when (type) {
            "UPC-A"     ->  return isUPCValid(content, 11)
            "UPC-E"     ->  return isUPCValid(content, 7)
            "EAN-8"     ->  return isEANValid(content,7)
            "EAN-13"    ->  return isEANValid(content, 12)
            "Code 128"  ->  return isCode128Valid(content)
            "QR Code"   ->  return true
            else  -> return false
        }
    }
}