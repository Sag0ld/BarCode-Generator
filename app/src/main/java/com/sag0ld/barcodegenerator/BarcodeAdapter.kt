package com.sag0ld.barcodegenerator

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.view.LayoutInflater
import com.sag0ld.barcodegenerator.database.Barcode
import java.util.*
import android.widget.RelativeLayout
import com.sag0ld.barcodegenerator.viewModels.BarcodeViewModel


class BarcodeAdapter(val context: Context): RecyclerView.Adapter<BarcodeAdapter.BarcodeViewHolder>() {

    var listener: IHistoryFragementListener? = null

    var barcodes = ArrayList<Barcode>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BarcodeViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_barcode, parent, false)
        return BarcodeViewHolder(view)
    }

    override fun getItemCount(): Int {
        return barcodes.size
    }

    override fun onBindViewHolder(holder: BarcodeViewHolder, position: Int) {
        holder.bind(barcodes[position])
        holder.view.setOnClickListener {
            listener?.showCodeInformation(barcodes[position])
        }
        holder.view.setOnLongClickListener {
            listener?.deleteCode(barcodes[position])

            true
        }
    }

    fun removeItem(adapterPosition: Int) {
        barcodes.removeAt(adapterPosition)
        notifyItemRemoved(adapterPosition)
    }

    fun restoreItem(deletedItem: Barcode, deletedIndex: Int) {
        barcodes.add(deletedIndex, deletedItem)
        listener?.addBarcode(deletedItem)
        notifyDataSetChanged()
    }

    inner class BarcodeViewHolder(val view: View): RecyclerView.ViewHolder(view) {
        val viewBackground = view.findViewById<RelativeLayout>(R.id.view_background)
        val viewForeground = view.findViewById<RelativeLayout>(R.id.view_foreground)
        val barcodeImageView = view.findViewById<ImageView>(R.id.barcodeImageView)
        val barcodeTypeTextView = view.findViewById<TextView>(R.id.barcodeTypeTextView)
        val barcodeContentTextView = view.findViewById<TextView>(R.id.barcodeContentTextView)
        val createAtDateTextView = view.findViewById<TextView>(R.id.createAtDateTextView)
        val createAtHoursTextView = view.findViewById<TextView>(R.id.createAtHoursTextView)

        fun bind(barcode: Barcode) {
            if (barcode.isQrCode())
                barcodeImageView.setImageResource(R.drawable.ic_qr_code)
            else
                barcodeImageView.setImageResource(R.drawable.ic_product_barcode)

            barcodeTypeTextView.text = barcode.type
            barcodeContentTextView.text = barcode.content
            createAtDateTextView.text = barcode.createAtDatetoString()
            createAtHoursTextView.text = barcode.createAtHourtoString()
        }
    }
}

interface IHistoryFragementListener {
    fun showCodeInformation(barcode: Barcode)
    fun deleteCode(barcode: Barcode)
    fun addBarcode(deletedItem: Barcode) {
    }
}