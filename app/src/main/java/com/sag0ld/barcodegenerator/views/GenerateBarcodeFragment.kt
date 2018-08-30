package com.sag0ld.barcodegenerator.views

import android.content.Context
import android.content.ContextWrapper
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.support.v4.app.Fragment
import android.text.*
import android.text.style.ForegroundColorSpan
import android.view.*
import android.widget.*
import com.sag0ld.barcodegenerator.*

import com.sag0ld.barcodegenerator.barcodes.AbstractBarcode
import com.sag0ld.barcodegenerator.database.AppDatabase
import com.sag0ld.barcodegenerator.database.Barcode
import kotlinx.android.synthetic.main.fragment_generate_barcode.*
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.*

class GenerateBarcodeFragment : Fragment(), AsyncResponse {

    private var listener: OnGenerateBarcodeFragmentListener? = null
    private val errorsMessages = StringBuilder()
    private var contentEditText : EditText? = null
    private var currentBarcode: AbstractBarcode? = null

    companion object {
        val TAG = GenerateBarcodeFragment.javaClass.canonicalName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        GenerateBarcodeTask.listener = this
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.fragment_generate_barcode, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        contentEditText = contentTextInputLayout?.editText

        val adapter : ArrayAdapter<CharSequence> = ArrayAdapter.createFromResource(context,
                R.array.type_of_barcode,android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        barcodeTypeSpinner?.adapter = adapter

        barcodeTypeSpinner?.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) { }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val type = barcodeTypeSpinner.selectedItem.toString()
                Controller.instance.createBarcodeBox(type)
                typeDescription.text = Controller.instance.getBarcodeDescription()

                when (type) {
                    "QR Code", "Code 128" -> contentEditText?.inputType = InputType.TYPE_CLASS_TEXT
                    else ->  contentEditText?.inputType = InputType.TYPE_CLASS_NUMBER
                }

                // Trim the content of the editText if is longer
                val maxLength = getMaxLength()
                contentEditText?.let {
                    // Set limit input
                    val filterArray = arrayOfNulls<InputFilter>(1)
                    filterArray[0] = InputFilter.LengthFilter(maxLength)
                    it.filters = filterArray

                    // Update the limit of editText
                    updateCounterMessage(it)

                    val content = it.text.toString()

                    // Generate a AbstractBarcode
                    if (isValid(type, content)) {
                        try {
                            GenerateBarcodeTask(progressBarHolder)
                                    .execute(type, content)
                        } catch (e: Exception) {
                            // Show errors message from API
                            Toast.makeText(context, e.toString(),
                                    Toast.LENGTH_SHORT).show()
                        }
                    }

                    if (errorsMessages.isNotEmpty()) {
                        // Show errors messages from the front end
                        Toast.makeText(context, errorsMessages.toString(),
                                Toast.LENGTH_SHORT).show()
                        // Erase messages
                        errorsMessages.delete(0, errorsMessages.lastIndex + 1)
                    }
                }
            }
        }

        // Init the content editText event
        contentEditText?.addTextChangedListener( object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (!p0.isNullOrEmpty()) {
                    contentEditText?.let { updateCounterMessage(it) }

                    val type = barcodeTypeSpinner.selectedItem.toString()
                    if(type == "Code 128" || type == "QR Code") {
                        lastTextEdit = System.currentTimeMillis()
                        try {
                            handler.postDelayed(inputFinishChecker, delay)
                        }
                        catch (e: Exception) {
                            // Show errors message from API
                            Toast.makeText(context, e.message,
                                    Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            override fun onTextChanged(content: CharSequence?, start: Int, before: Int, count: Int) {
                val type = barcodeTypeSpinner.selectedItem.toString()

                // Generate a AbstractBarcode and set the imageView
                if (isValid(type, content.toString())) {
                    try {
                        if (type == "Code 128" || type == "QR Code") {
                            handler.removeCallbacks(inputFinishChecker)
                        }
                        else {
                            GenerateBarcodeTask(progressBarHolder)
                                    .execute(type, content?.toString())
                        }
                    } catch (e: Exception) {
                        // Show errors message from API
                        Toast.makeText(context, e.message,
                                Toast.LENGTH_SHORT).show()
                    }
                } else if (errorsMessages.isNotEmpty()) {
                    // Show errors messages from the front end
                    Toast.makeText(context, errorsMessages.toString(),
                            Toast.LENGTH_SHORT).show()
                    // Erase messages
                    errorsMessages.delete(0, errorsMessages.lastIndex + 1)
                }
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }
        })
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnGenerateBarcodeFragmentListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnGenerateBarcodeFragmentListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.generate_barcode_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_save_barcode -> {
                currentBarcode?.let {
                    saveToDatabase(it)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun processFinish(output: Bitmap) {
        Toast.makeText(context, "SET", Toast.LENGTH_SHORT).show()
        barcodeView.setImageBitmap(output)
        currentBarcode = Controller.instance.getBarcode()
    }

    private fun bitmapToFile(bitmap: Bitmap): Uri {
        val wrapper = ContextWrapper(context)

        // Initialize a new file instance to save bitmap object
        var file = wrapper.getDir("Images",Context.MODE_PRIVATE)
        file = File(file,"${UUID.randomUUID()}.jpg")

        try{
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,stream)
            stream.flush()
            stream.close()
        }catch (e: IOException){
            e.printStackTrace()
        }

        // Return the saved bitmap uri
        return Uri.parse(file.absolutePath)
    }

    private fun saveToDatabase(currentBarcode: AbstractBarcode) {
        currentBarcode.generate()?.let { bitmap ->
            val barcode = Barcode()
            barcode.content = currentBarcode.content
            barcode.createAt = currentBarcode.createAt?.timeInMillis

            barcode.uri = bitmapToFile(bitmap).path
            context?.let {
                AppDatabase.getAppDatabase(it).userDao().insert(barcode)
            }
        }
    }

    fun getMaxLength()  : Int {
        val type = barcodeTypeSpinner.selectedItem.toString()
        val maxLength: Int
        maxLength = when (type) {
            "UPC-A" -> 11
            "UPC-E", "EAN-8" -> 7
            "EAN-13" -> 12
            "Code 128" -> 80
            "QR Code" -> 9999
            else -> 0
        }
        return maxLength
    }

    fun updateCounterMessage (contentEditText : EditText) {
        val counter = SpannableStringBuilder()
        counter.clear()
        counter.clearSpans()

        val length = contentEditText.length()
        if (length > 0) {
            // Set the max length for the AbstractBarcode type selected
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

    private fun isFirstChar0ou1(content : String): Boolean {
        if (!content[0].equals('0') && !content[0].equals('1')) {
            errorsMessages.append("The content must begin with 0 or 1")
            return false
        }
        return true
    }

    private fun isEANValid (content : String, limit : Int) : Boolean {
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
        return when (type) {
            "UPC-A"     -> isUPCValid(content, 11)
            "UPC-E"     -> isUPCValid(content, 7) && isFirstChar0ou1(content)
            "EAN-8"     -> isEANValid(content,7)
            "EAN-13"    -> isEANValid(content, 12)
            "Code 128"  -> isCode128Valid(content)
            "QR Code"   -> true
            else  -> false
        }
    }

    var delay: Long = 2000 // 2 seconds after user stops typing
    var lastTextEdit: Long = 0
    var handler = Handler()

    private val inputFinishChecker = Runnable {
        val type = barcodeTypeSpinner.selectedItem.toString()
        if (System.currentTimeMillis() > lastTextEdit + delay - 500) {
            contentEditText?.let {
                if (isValid(type, it.text.toString())) {
                    GenerateBarcodeTask(progressBarHolder)
                            .execute(type, it.text.toString())

                }
            }
        }
    }

    interface OnGenerateBarcodeFragmentListener
}