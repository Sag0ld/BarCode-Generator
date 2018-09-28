package com.sag0ld.barcodegenerator.views

import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.sag0ld.barcodegenerator.App
import com.sag0ld.barcodegenerator.BarcodeAdapter
import com.sag0ld.barcodegenerator.IHistoryFragementListener

import com.sag0ld.barcodegenerator.R
import com.sag0ld.barcodegenerator.database.Barcode
import kotlinx.android.synthetic.main.fragment_history.*
import java.util.*

class HistoryFragment : Fragment(), IHistoryFragementListener {


    companion object {
        val TAG = HistoryFragment.javaClass.canonicalName
    }

    private var listener: GenerateBarcodeFragment.OnGenerateBarcodeFragmentListener? = null
    lateinit var adapter: BarcodeAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adapter = BarcodeAdapter(App.instance.applicationContext)
        adapter.listener = this
        noContentTextView?.visibility = View.VISIBLE
        listener?.getBarcodes()?.observe(this, Observer<List<Barcode>> {barcodes ->
            barcodes?.let {
                adapter.barcodes = ArrayList(it)
            }
        })
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
        Toast.makeText(context, "Barcode"+barcode.content, Toast.LENGTH_SHORT).show()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is GenerateBarcodeFragment.OnGenerateBarcodeFragmentListener) {
            listener = context
        } else {
            throw RuntimeException(context.toString() + " must implement OnGenerateBarcodeFragmentListener")
        }
    }

    override fun onDetach() {
        super.onDetach()
        listener = null
    }

    interface OnFragmentInteractionListener
}