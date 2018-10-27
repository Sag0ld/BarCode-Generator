package com.sag0ld.barcodegenerator.views

import android.arch.lifecycle.*
import android.arch.lifecycle.Observer
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.text.*
import android.text.style.ForegroundColorSpan
import android.view.*
import android.widget.*
import com.bumptech.glide.Glide
import com.sag0ld.barcodegenerator.*
import com.sag0ld.barcodegenerator.R

import com.sag0ld.barcodegenerator.barcodes.AbstractBarcode
import com.sag0ld.barcodegenerator.database.Barcode
import com.sag0ld.barcodegenerator.viewModels.BarcodeViewModel
import kotlinx.android.synthetic.main.fragment_generate_barcode.*
import org.jetbrains.anko.doAsync

class GenerateBarcodeFragment : Fragment() {

    private var contentEditText : EditText? = null
    private var model : BarcodeViewModel? = null
    private lateinit var typeAdapter : ArrayAdapter<CharSequence>

    companion object {
        val TAG = GenerateBarcodeFragment.javaClass.canonicalName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        activity?.let {
            model = ViewModelProviders.of(it).get(BarcodeViewModel::class.java)
        }

        typeAdapter = ArrayAdapter.createFromResource(context, R.array.type_of_barcode,
                                                 android.R.layout.simple_spinner_item)
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_generate_barcode, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        saveButton?.setOnClickListener {
            progressBarHolder?.visibility = View.VISIBLE
            model?.currentBarcodeLiveData?.value?.let { code ->
                saveToDatabase(code)
            }
        }

        barcodeTypeSpinner?.adapter = typeAdapter
        barcodeTypeSpinner?.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) { }

            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val type = barcodeTypeSpinner.selectedItem.toString()
                model?.currentBarcodeLiveData?.value = Controller.instance.createBarcodeEntity(type)
                typeDescription?.text = Controller.instance.getBarcodeDescription()

                when (type) {
                    "QR Code", "Code 128" -> contentEditText?.inputType = InputType.TYPE_CLASS_TEXT
                    else ->  contentEditText?.inputType = InputType.TYPE_CLASS_NUMBER
                }

                model?.let { model ->
                   model.currentBarcodeLiveData.value?.let {  barcode ->
                     val maxLength = barcode.maxLength
                       contentEditText?.let {
                           // Set limit input
                           val filterArray = arrayOfNulls<InputFilter>(1)
                           filterArray[0] = InputFilter.LengthFilter(maxLength)
                           it.filters = filterArray

                           // Update the limit of editText
                           updateCounterMessage(it)
                           val content = it.text.toString()
                           if (content.isNotEmpty()) {
                               if (barcode.isValid(content)) {
                                   try {
                                       updateBarcodeImageViewSource(type, content)
                                   } catch (e: Exception) {
                                       // Show errors message from API
                                       Toast.makeText(context, e.toString(),
                                               Toast.LENGTH_SHORT).show()
                                   }
                               } else if (barcode.errors.isNotEmpty()) {
                                   Toast.makeText(context, barcode.getErrorsMessage(),
                                           Toast.LENGTH_SHORT).show()
                               }
                           }
                       }
                   }
                }
            }
        }

        contentEditText = contentTextInputLayout?.editText

        // Init the content editText event
        contentEditText?.addTextChangedListener( object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                handler.removeCallbacks(inputFinishChecker)

                if (!p0.isNullOrEmpty()) {
                    contentEditText?.let { updateCounterMessage(it) }
                    model?.content = p0.toString()

                    val type = barcodeTypeSpinner?.selectedItem.toString()
                    if(type == "Code 128" || type == "QR Code") {
                        try {
                            handler.postDelayed(inputFinishChecker, delay)
                        }
                        catch (e: Exception) {
                            Toast.makeText(context, e.message,
                                    Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            }

            override fun onTextChanged(content: CharSequence?, start: Int, before: Int, count: Int) {
                val type = barcodeTypeSpinner?.selectedItem.toString()
                content?.let {
                    // Generate a AbstractBarcode and set the imageView
                    model?.currentBarcodeLiveData?.value?.let { barcode ->
                        if (barcode.maxLength == it.length) {
                            if (barcode.isValid(content.toString())) {
                                try {
                                    if (type != "Code 128" || type != "QR Code") {
                                        updateBarcodeImageViewSource(type, content.toString())
                                    }
                                } catch (e: Exception) {
                                    // Show errors message from API
                                    Toast.makeText(context, e.message,
                                            Toast.LENGTH_SHORT).show()
                                }
                            } else if (barcode.errors.isNotEmpty()) {
                                // Show errors messages from the front end
                                Toast.makeText(context, barcode.getErrorsMessage(),
                                        Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }
        })
    }

    override fun onResume() {
        super.onResume()
        model?.let { model ->
            model.currentBitmapLiveData.observe(context as LifecycleOwner, Observer<Bitmap> {
                context?.let { context ->
                    Glide.with(context)
                        .load(it)
                        .into(barcodeImageView)
                    progressBarHolder?.visibility = View.GONE
                }
            })

            contentEditText?.setText(model.content)
            model.currentBarcodeLiveData.value?.let { barcode ->
                val position = typeAdapter.getPosition(barcode.toString())
                barcodeTypeSpinner?.setSelection(position)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.generate_barcode_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_scan_code -> {
                Toast.makeText(context, "Switch to scanner", Toast.LENGTH_SHORT).show()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun saveToDatabase(currentBarcode: AbstractBarcode) {
        model?.let { model ->
            val barcode = Barcode()
            barcode.content = model.content
            barcode.createAt = currentBarcode.createAt?.timeInMillis
            barcode.type = currentBarcode.toString()

            model.addBarcode(barcode)
            view?.let {
                Snackbar.make(it, "Saved", Snackbar.LENGTH_SHORT).show()
            }
        }
        progressBarHolder?.visibility = View.GONE
    }

    fun updateCounterMessage (contentEditText : EditText) {
        model?.currentBarcodeLiveData?.value?.let { barcode ->
            val counter = SpannableStringBuilder()
            counter.clear()
            counter.clearSpans()

            val length = contentEditText.length()
            if (length > 0) {
                // Set the max length for the AbstractBarcode type selected
                val maxLength = barcode.maxLength

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
    }

    private fun updateBarcodeImageViewSource(type: String, content: String) {
        progressBarHolder?.visibility = View.VISIBLE
        model?.content = content
        doAsync {
            model?.let { model ->
                model.currentBitmapLiveData.postValue(Controller.instance.generateBitmap(type, content))
                model.currentBarcodeLiveData.postValue(Controller.instance.getBarcode())
            }
        }
    }

    var delay: Long = 2000
    var handler = Handler()

    private val inputFinishChecker = Runnable {
        val type = barcodeTypeSpinner?.selectedItem.toString()
        if (System.currentTimeMillis() >  delay - 500) {
            contentEditText?.let {
                model?.currentBarcodeLiveData?.value?.let { barcode ->
                    if (barcode.isValid(it.text.toString()))
                        updateBarcodeImageViewSource(type, it.text.toString())
                    else {
                        Toast.makeText(context, barcode.getErrorsMessage(),
                                Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}