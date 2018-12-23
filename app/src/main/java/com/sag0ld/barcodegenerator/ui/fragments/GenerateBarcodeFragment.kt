package com.sag0ld.barcodegenerator.ui.fragments

import android.arch.lifecycle.*
import android.arch.lifecycle.Observer
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v4.app.Fragment
import android.text.*
import android.text.style.ForegroundColorSpan
import android.view.*
import android.widget.*
import com.github.jorgecastilloprz.listeners.FABProgressListener
import com.sag0ld.barcodegenerator.R

import com.sag0ld.barcodegenerator.domain.barcodes.AbstractBarcode
import com.sag0ld.barcodegenerator.domain.barcodes.CodeFactory
import com.sag0ld.barcodegenerator.domain.models.Code
import com.sag0ld.barcodegenerator.ui.viewModels.BarcodeViewModel
import com.sag0ld.barcodegenerator.util.GlideApp
import kotlinx.android.synthetic.main.fragment_generate_barcode.*
import org.jetbrains.anko.doAsync

class GenerateBarcodeFragment : Fragment(), FABProgressListener {

    private var contentEditText: EditText? = null
    private var model: BarcodeViewModel? = null
    private var isSaveBtnLocked = false
    private lateinit var typeAdapter: ArrayAdapter<CharSequence>

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
            if (!isSaveBtnLocked) {
                model?.currentBarcodeLiveData?.value?.let { code ->
                    isSaveBtnLocked = true
                    saveButtonProgress.show()
                    saveButtonProgress.attachListener(this)
                    saveToDatabase(code)
                }
            }
        }

        barcodeTypeSpinner?.adapter = typeAdapter
        barcodeTypeSpinner?.onItemSelectedListener = itemSelectedListener

        contentEditText = contentTextInputLayout?.editText
        contentEditText?.addTextChangedListener(textWatcher)
    }

    private val itemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(p0: AdapterView<*>?) {}

        override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
            val type = barcodeTypeSpinner.selectedItem.toString()
            val barcode = CodeFactory.instance.createBarcodeEntity(type)
            model?.currentBarcodeLiveData?.value = barcode
            typeDescription?.text = barcode.description

            val maxLength = barcode.maxLength
            contentEditText?.let {
                when (type) {
                    "QR Code", "Code 128" -> it.inputType = InputType.TYPE_CLASS_TEXT
                    else -> it.inputType = InputType.TYPE_CLASS_NUMBER
                }
                // Set limit input
                val filterArray = arrayOfNulls<InputFilter>(1)
                filterArray[0] = InputFilter.LengthFilter(maxLength)
                it.filters = filterArray

                // Update the limit of editText
                updateCounterMessage(it.text.length)
                barcode.content = it.text.toString()
            }

            updateBarcodeImageViewSource(barcode)
        }
    }

    private val textWatcher = object: TextWatcher {
        override fun afterTextChanged(editableText: Editable) {
            handler.removeCallbacks(inputFinishChecker)
            updateCounterMessage(editableText.length)
            model?.let { model ->
               model.content = editableText.toString()
                model.currentBarcodeLiveData.value?.let { barcode ->
                    barcode.content = model.content
                    if(barcode.hasDelayToGenerateBitmap()) {
                        try {
                            handler.postDelayed(inputFinishChecker, delay)
                        }
                        catch (e: Exception) {
                            Toast.makeText(context, e.message,
                                    Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        updateBarcodeImageViewSource(barcode)
                    }
                }
            }
        }

        override fun onTextChanged(content: CharSequence?, start: Int, before: Int, count: Int) { }
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }
    }

    override fun onResume() {
        super.onResume()
        model?.let { model ->
            model.currentBitmapLiveData.observe(context as LifecycleOwner, Observer<Bitmap> {
                context?.let { context ->
                    GlideApp.with(context)
                        .load(it)
                            .error(R.drawable.ic_error_loading)
                        .into(barcodeImageView)
                    progressBarHolder?.visibility = View.GONE
                }
            })

            contentEditText?.removeTextChangedListener(textWatcher)
            contentEditText?.setText(model.content)
            contentEditText?.addTextChangedListener(textWatcher)

            model.currentBarcodeLiveData.value?.let { barcode ->
                val position = typeAdapter.getPosition(barcode.toString())
                barcodeTypeSpinner?.onItemSelectedListener = null
                barcodeTypeSpinner?.setSelection(position, false)
                barcodeTypeSpinner?.onItemSelectedListener = itemSelectedListener
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

    override fun onFABProgressAnimationEnd() {
        isSaveBtnLocked = false
    }

    private fun saveToDatabase(currentBarcode: AbstractBarcode) {
        model?.let { model ->
            val barcode = Code()
            barcode.content = model.content
            barcode.createAt = currentBarcode.createAt?.timeInMillis
            barcode.type = currentBarcode.toString()

            model.addBarcode(barcode)
            saveButtonProgress.beginFinalAnimation()
        }
    }

    fun updateCounterMessage (currentContentSize: Int) {
        model?.currentBarcodeLiveData?.value?.let { barcode ->
            val counter = SpannableStringBuilder()
            counter.clear()
            counter.clearSpans()

            // Set the max length for the AbstractBarcode type selected
            val maxLength = barcode.maxLength

            counter.append("$currentContentSize/$maxLength")
            if (currentContentSize < maxLength)
                counter.setSpan(ForegroundColorSpan(Color.GRAY), 0, counter.length,
                        Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
            if (currentContentSize == maxLength)
                counter.setSpan(ForegroundColorSpan(Color.GREEN), 0, counter.length,
                        Spanned.SPAN_INCLUSIVE_EXCLUSIVE)

            contentTextInputLayout.error = counter
        }
    }

    private fun updateBarcodeImageViewSource(barcode: AbstractBarcode) {
        if (barcode.isValid()) {
            progressBarHolder?.visibility = View.VISIBLE
            doAsync {
                model?.let { model ->
                    model.currentBitmapLiveData.postValue(barcode.generate())
                    model.currentBarcodeLiveData.postValue(barcode)
                }
            }
        } else if (barcode.errors.isNotEmpty()) {
           Toast.makeText(context, barcode.getErrorsMessage(),
                   Toast.LENGTH_SHORT).show()
        }
    }

    var delay: Long = 2000
    var handler = Handler(Looper.myLooper())

    private val inputFinishChecker = object: Runnable {
        override fun run() {
            handler.removeCallbacks(this)
            if (System.currentTimeMillis() >  delay - 500) {
                model?.currentBarcodeLiveData?.value?.let { barcode ->
                    updateBarcodeImageViewSource(barcode)
                }
            }
        }
    }
}