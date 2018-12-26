package com.sag0ld.barcodegenerator.ui.fragment

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v4.app.Fragment
import android.text.*
import android.text.style.ForegroundColorSpan
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast

import com.sag0ld.barcodegenerator.R
import com.sag0ld.barcodegenerator.data.barcodes.AbstractBarcode
import com.sag0ld.barcodegenerator.domain.Barcode
import com.sag0ld.barcodegenerator.ui.viewModels.CodeDetailsViewModel
import com.sag0ld.barcodegenerator.util.BarcodeAssembler
import com.sag0ld.barcodegenerator.util.BarcodeFactory
import kotlinx.android.synthetic.main.fragment_edit.*
import org.jetbrains.anko.doAsync

class EditFragment : Fragment() {
    private var listener: OnFragmentInteractionListener? = null
    private var model: CodeDetailsViewModel? = null
    private lateinit var typeAdapter: ArrayAdapter<CharSequence>
    private var contentEditText: EditText? = null
    private var barcode :AbstractBarcode? = null

    companion object {
        val TAG = EditFragment.javaClass.canonicalName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        activity?.let {
            model = ViewModelProviders.of(it).get(CodeDetailsViewModel::class.java)
        }

        typeAdapter = ArrayAdapter.createFromResource(context, R.array.type_of_barcode,
                android.R.layout.simple_spinner_item)
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
       return inflater.inflate(R.layout.fragment_edit, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        barcodeTypeSpinner?.adapter = typeAdapter
        barcodeTypeSpinner?.onItemSelectedListener = itemSelectedListener

        contentEditText = contentTextInputLayout?.editText
        contentEditText?.addTextChangedListener(textWatcher)
    }

    private val itemSelectedListener = object : AdapterView.OnItemSelectedListener {
        override fun onNothingSelected(p0: AdapterView<*>?) {}

        override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
            val type = barcodeTypeSpinner.selectedItem.toString()
            barcode = BarcodeFactory.instance.createBarcodeEntity(type)
            typeDescription?.text = barcode!!.description

            val maxLength = barcode!!.maxLength
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
                barcode!!.content = it.text.toString()
            }

            updateBarcodeImageViewSource(barcode!!)
        }
    }

    private val textWatcher = object: TextWatcher {
        override fun afterTextChanged(editableText: Editable) {
            handler.removeCallbacks(inputFinishChecker)
            updateCounterMessage(editableText.length)
            barcode?.let { barcode ->
                barcode.content = editableText.toString()
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

        override fun onTextChanged(content: CharSequence?, start: Int, before: Int, count: Int) { }
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) { }
    }

    fun updateCounterMessage (currentContentSize: Int) {
        val counter = SpannableStringBuilder()
        counter.clear()
        counter.clearSpans()

        barcode?.let {barcode ->
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
            doAsync {
                model?.bitmapLiveData?.postValue(barcode.generate())
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
            barcode?.let { barcode ->
                if (System.currentTimeMillis() >  delay - 500) {
                    updateBarcodeImageViewSource(barcode)
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        updateBarcode()
    }

    override fun onResume() {
        super.onResume()
        model?.barcodeLiveData?.observe(this, Observer<Barcode> {
            it?.let { barcode ->
                this.barcode = BarcodeAssembler.toAbstractBarcode(barcode)
                contentEditText?.removeTextChangedListener(textWatcher)
                contentEditText?.setText(barcode.content)
                contentEditText?.addTextChangedListener(textWatcher)

                val position = typeAdapter.getPosition(barcode.type)
                barcodeTypeSpinner?.onItemSelectedListener = null
                barcodeTypeSpinner?.setSelection(position, false)
                barcodeTypeSpinner?.onItemSelectedListener = itemSelectedListener
            }
        })
    }

    private fun updateBarcode() {
        barcode?.content?.let { content ->
            val type = barcodeTypeSpinner.selectedItem.toString()
            listener?.updateBarcode(content, type)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.edit_activity_menu, menu)
        return super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_save -> {
                updateBarcode()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is OnFragmentInteractionListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnFragmentInteractionListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnFragmentInteractionListener {
        fun updateBarcode(content: String, type: String)
    }
}