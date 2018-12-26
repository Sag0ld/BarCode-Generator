package com.sag0ld.barcodegenerator.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import com.sag0ld.barcodegenerator.R
import com.sag0ld.barcodegenerator.data.barcodes.AbstractBarcode
import com.sag0ld.barcodegenerator.domain.Barcode
import com.sag0ld.barcodegenerator.ui.fragment.EditFragment
import com.sag0ld.barcodegenerator.ui.fragment.ShowFragment
import com.sag0ld.barcodegenerator.ui.viewModels.CodeDetailsViewModel
import com.sag0ld.barcodegenerator.util.BarcodeAssembler
import com.sag0ld.barcodegenerator.util.BarcodeFactory
import com.sag0ld.barcodegenerator.util.GlideApp

import kotlinx.android.synthetic.main.content_code_details.*
import kotlinx.android.synthetic.main.activity_code_details.*
import org.jetbrains.anko.doAsync
import java.util.*

class DetailsActivity : AppCompatActivity(), ShowFragment.OnFragmentInteractionListener, EditFragment.OnFragmentInteractionListener{

    companion object {
        val EXTRA_ID = "BARCODE_ID"
    }

    private lateinit var model: CodeDetailsViewModel
    private var barcode: AbstractBarcode? = null
    private val showFragment = ShowFragment()
    private val editFragment = EditFragment()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_code_details)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        toolbar?.setNavigationOnClickListener {
            onBackPressed()
        }

        val transaction = supportFragmentManager.beginTransaction()
        transaction.add(R.id.frameFragmentManagerLayout, showFragment, ShowFragment.TAG)
        transaction.commit()

        val barcodeID = intent.getLongExtra(EXTRA_ID, 0L)
        model = ViewModelProviders.of(this).get(CodeDetailsViewModel::class.java)

        model.barcodeLiveData = model.getBarcode(barcodeID)
        model.barcodeLiveData.observe(this, Observer<Barcode> {
            it?.let { barcodeFound ->
                barcodeFound.type?.let { type ->
                    barcode = BarcodeFactory.instance.createBarcodeEntity(type)
                }
                barcode?.let { barcode ->
                    barcodeFound.content?.let { content ->
                        barcode.content = content
                        if (barcode.isValid()) {
                            doAsync {
                                model.bitmapLiveData.postValue(barcode.generate())
                            }
                        }
                    }
                    barcode.createAt = Calendar.getInstance()
                    barcodeFound.createAt?.let { lastUpdateInMillis ->
                        barcode.createAt?.timeInMillis = lastUpdateInMillis
                    }
                }
            }
        })
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
    }

    override fun editBarcode() {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frameFragmentManagerLayout, editFragment, editFragment.tag)
        transaction.addToBackStack(editFragment.tag)
        transaction.commit()
    }

    override fun deleteBarcode() {
        AlertDialog.Builder(this)
            .setMessage(R.string.delete_message)
            .setNegativeButton(R.string.no) { p0, _ ->
                p0.cancel()
            }
            .setPositiveButton(R.string.yes) { _, _ ->
                Toast.makeText(this, "Delete", Toast.LENGTH_LONG).show()
                model.deleteBarcode(model.barcodeLiveData.value!!)
                finish()
            }.show()
    }

    override fun updateBarcode(content: String, type: String) {
        model.barcodeLiveData.value?.let { barcode ->
            barcode.content = content
            barcode.type = type
            barcode.createAt = Calendar.getInstance().timeInMillis
            model.updateBarcode(barcode)
        }
    }



    override fun onAttachFragment(fragment: Fragment) {
        fragment.onAttach(this)
    }
}