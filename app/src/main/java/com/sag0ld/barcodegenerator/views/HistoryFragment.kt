package com.sag0ld.barcodegenerator.views

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.sag0ld.barcodegenerator.database.Barcode
import kotlinx.android.synthetic.main.fragment_history.*
import java.util.*
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.support.design.widget.Snackbar
import com.sag0ld.barcodegenerator.*
import com.sag0ld.barcodegenerator.viewModels.BarcodeViewModel

class HistoryFragment : Fragment(), IHistoryFragementListener {

    companion object {
        val TAG = HistoryFragment.javaClass.canonicalName
    }

    lateinit var adapter: BarcodeAdapter
    lateinit var model : BarcodeViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        activity?.let {
            adapter = BarcodeAdapter(App.instance.applicationContext)
            adapter.listener = this
            noContentTextView?.visibility = View.VISIBLE

            model = ViewModelProviders.of(it).get(BarcodeViewModel::class.java)
            model.getBarcodes().observe(this, Observer<List<Barcode>> { barcodes ->
                barcodes?.let { codes ->
                    adapter.barcodes = ArrayList(codes)
                }
            })
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_history, container, false)
        val barcodeRecyclerView = view.findViewById<RecyclerView>(R.id.barcodeRecyclerView)

        barcodeRecyclerView.adapter = adapter
        val viewManager = LinearLayoutManager(context)
        barcodeRecyclerView.layoutManager = viewManager
        if (adapter.barcodes.size > 0) {
            noContentTextView?.visibility = View.GONE
        }

        return view
    }

    override fun showCodeInformation(barcode: Barcode) {
        val intent = Intent(context, CodeDetailsActivity::class.java)
        intent.putExtra(CodeDetailsActivity.EXTRA_ID, barcode.id)
        startActivity(intent)
    }

    override fun deleteCode(barcode: Barcode) {
        view?.let {
            Snackbar.make(it, "Deleted", Snackbar.LENGTH_SHORT).show()
            model.deleteBarcode(barcode)
        }
    }
}