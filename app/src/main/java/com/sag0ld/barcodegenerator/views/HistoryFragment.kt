package com.sag0ld.barcodegenerator.views

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sag0ld.barcodegenerator.database.Barcode
import kotlinx.android.synthetic.main.fragment_history.*
import java.util.*
import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.graphics.Color
import android.support.design.widget.Snackbar
import android.support.v7.app.AlertDialog
import com.sag0ld.barcodegenerator.*
import com.sag0ld.barcodegenerator.viewModels.BarcodeViewModel
import android.support.v7.widget.helper.ItemTouchHelper
import com.sag0ld.barcodegenerator.RecyclerItemTouchHelper



class HistoryFragment : Fragment(), IHistoryFragementListener, RecyclerItemTouchHelper.RecyclerItemTouchHelperListener {

    companion object {
        val TAG = HistoryFragment.javaClass.canonicalName
    }

    lateinit var adapter: BarcodeAdapter
    lateinit var model: BarcodeViewModel


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

        val itemTouchHelperCallback = RecyclerItemTouchHelper(
                0, ItemTouchHelper.LEFT, this)
        ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(barcodeRecyclerView)
        return view
    }

    override fun showCodeInformation(barcode: Barcode) {
        val intent = Intent(context, CodeDetailsActivity::class.java)
        intent.putExtra(CodeDetailsActivity.EXTRA_ID, barcode.id)
        startActivity(intent)
    }

    override fun deleteCode(barcode: Barcode) {
        view?.let { view ->
            context?.let {
                AlertDialog.Builder(it)
                    .setMessage(R.string.delete_message)
                    .setNegativeButton(R.string.no) { p0, _ ->
                        p0.cancel()
                    }
                    .setPositiveButton(R.string.yes) { _, _ ->
                        Snackbar.make(view, "Deleted", Snackbar.LENGTH_SHORT).show()
                        model.deleteBarcode(barcode)
                    }.show()
            }
        }
    }

    override fun addBarcode(deletedItem: Barcode) {
        model.addBarcode(deletedItem)
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int, position: Int) {
        if (viewHolder is BarcodeAdapter.BarcodeViewHolder) {
            val deletedItem = adapter.barcodes[viewHolder.getAdapterPosition()]
            val content = deletedItem.content
            val deletedIndex = viewHolder.getAdapterPosition()

            adapter.removeItem(viewHolder.getAdapterPosition())
            model.deleteBarcode(deletedItem)

            view?.let { view ->
                Snackbar.make(view, "$content removed from history", Snackbar.LENGTH_INDEFINITE)
                        .setAction("UNDO") { adapter.restoreItem(deletedItem, deletedIndex); }
                        .setActionTextColor(Color.WHITE)
                        .show()
            }
        }
    }
}