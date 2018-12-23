package com.sag0ld.barcodegenerator.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.sag0ld.barcodegenerator.R
import com.sag0ld.barcodegenerator.domain.barcodes.AbstractBarcode
import com.sag0ld.barcodegenerator.domain.barcodes.CodeFactory
import com.sag0ld.barcodegenerator.domain.models.Code
import com.sag0ld.barcodegenerator.ui.viewModels.CodeDetailsViewModel
import com.sag0ld.barcodegenerator.util.GlideApp

import kotlinx.android.synthetic.main.content_code_details.*
import kotlinx.android.synthetic.main.activity_code_details.*
import org.jetbrains.anko.doAsync
import java.util.*

class CodeDetailsActivity : AppCompatActivity() {

    companion object {
        val EXTRA_ID = "BARCODE_ID"
    }

    private lateinit var model: CodeDetailsViewModel
    private var codeDetails: AbstractBarcode? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_code_details)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        toolbar?.setNavigationOnClickListener {
            onBackPressed()
        }

        val barcodeID = intent.getLongExtra(EXTRA_ID, 0L)
        model = ViewModelProviders.of(this).get(CodeDetailsViewModel::class.java)

        model.codeLiveData = model.getBarcode(barcodeID)
        model.codeLiveData.observe(this, Observer<Code> {
            it?.let { code ->
                code.type?.let { type ->
                    codeDetails = CodeFactory.instance.createBarcodeEntity(type)
                }
                codeDetails?.let { code ->
                    code.content?.let { content ->
                        code.content = content
                        if (code.isValid()) {
                            doAsync {
                                model.bitmapLiveData.postValue(code.generate())
                            }
                        }
                    }
                    code.createAt = Calendar.getInstance()
                    code.createAt?.let { lastUpdateInMillis ->
                        code.createAt?.timeInMillis = lastUpdateInMillis
                    }
                }
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.details_code_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.action_delete -> {
                AlertDialog.Builder(this)
                    .setMessage(R.string.delete_message)
                    .setNegativeButton(R.string.no) { p0, _ ->
                        p0.cancel()
                    }
                    .setPositiveButton(R.string.yes) { _, _ ->
                        Toast.makeText(this, "Delete", Toast.LENGTH_LONG).show()
                        model.deleteBarcode(model.codeLiveData.value!!)
                        finish()
                    }.show()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        model.bitmapLiveData.observe(this, Observer<Bitmap> {
            it?.let { bitmap ->
                GlideApp.with(this)
                        .load(bitmap)
                        .error(R.drawable.ic_error_loading)
                        .into(codeImageViewDetails)
            }
        })

        model.codeLiveData.observe(this, Observer<Code> {
            it?.let { code ->
                codeTypeTextView?.text = code.type
                codeContentTextView?.text = code.content
                codeDateTextView?.text = code.createAtDatetoString()
            }
        })
    }
}