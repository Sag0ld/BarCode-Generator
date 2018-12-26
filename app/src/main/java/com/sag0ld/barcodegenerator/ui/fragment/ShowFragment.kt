package com.sag0ld.barcodegenerator.ui.fragment

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.*

import com.sag0ld.barcodegenerator.R
import com.sag0ld.barcodegenerator.domain.Barcode
import com.sag0ld.barcodegenerator.ui.viewModels.CodeDetailsViewModel
import kotlinx.android.synthetic.main.fragment_show.*


class ShowFragment : Fragment() {
    private var listener: OnFragmentInteractionListener? = null
    private var model: CodeDetailsViewModel? = null

    companion object {
        val TAG = ShowFragment.javaClass.canonicalName
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)

        activity?.let {
            model = ViewModelProviders.of(it).get(CodeDetailsViewModel::class.java)
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_show, container, false)
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.details_code_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
            when (item?.itemId) {
            R.id.action_delete -> {
                listener?.deleteBarcode()
            }
            R.id.action_edit -> {
                listener?.editBarcode()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        model?.barcodeLiveData?.observe(this, Observer<Barcode> {
            it?.let { barcode ->
                codeTypeTextView?.text = barcode.type
                codeContentTextView?.text = barcode.content
                codeDateTextView?.text = barcode.createAtDatetoString()
            }
        })
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
        fun deleteBarcode()
        fun editBarcode()
    }
}
