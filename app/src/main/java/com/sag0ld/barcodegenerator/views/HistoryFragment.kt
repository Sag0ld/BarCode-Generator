package com.sag0ld.barcodegenerator.views

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sag0ld.barcodegenerator.BarcodeAdapter

import com.sag0ld.barcodegenerator.R
import com.sag0ld.barcodegenerator.database.Barcode
import kotlinx.android.synthetic.main.fragment_history.*

class HistoryFragment : Fragment() {

    companion object {
        val TAG = HistoryFragment.javaClass.canonicalName
    }

    private var listener: OnFragmentInteractionListener? = null
    private lateinit var adapter: BarcodeAdapter
    var barcodes = MutableLiveData<List<Barcode>>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_history, container, false)

        context?.let {
            adapter = BarcodeAdapter(it)
            barcodeRecyclerView?.adapter = adapter

            barcodes.observe(context as LifecycleOwner, Observer<List<Barcode>> { barcodes ->
                barcodes?.let {
                    adapter.barcodes = it.toMutableList()
                }
            })
        }
        return view
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

    interface OnFragmentInteractionListener
}
