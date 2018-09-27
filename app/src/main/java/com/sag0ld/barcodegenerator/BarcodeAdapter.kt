package com.sag0ld.barcodegenerator

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import android.view.LayoutInflater
import com.sag0ld.barcodegenerator.database.Barcode
import java.util.*

class BarcodeAdapter(val context: Context): RecyclerView.Adapter<BarcodeAdapter.BarcodeViewHolder>() {

    var listener: IHistoryFragementListener? = null

    var barcodes = ArrayList<Barcode>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BarcodeViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val v = inflater.inflate(R.layout.item_barcode, parent, false)
        return BarcodeViewHolder(v)
    }

    override fun getItemCount(): Int {
        return barcodes.size
    }

    override fun onBindViewHolder(holder: BarcodeViewHolder, position: Int) {
        holder.bind(barcodes[position])
        holder.view.setOnClickListener {
            listener?.showCodeInformation(barcodes[position])
        }
    }

    inner class BarcodeViewHolder(val view: View): RecyclerView.ViewHolder(view) {

        val barcodeImageView = view.findViewById<ImageView>(R.id.barcodeImageView)
        val barcodeContentTextView = view.findViewById<TextView>(R.id.barcodeContentTextView)
        val createAtDateTextView = view.findViewById<TextView>(R.id.createAtDateTextView)
        val barcodeTypeTextView = view.findViewById<TextView>(R.id.barcodeTypeTextView)
        val createAtHoursTextView = view.findViewById<TextView>(R.id.createAtHoursTextView)

        fun bind(barcode: Barcode) {
            if (barcode.isQrCode()) {
                Glide.with(context)
                        .load(R.drawable.ic_qr_code)
                        .into(barcodeImageView)
            }
            else {
                Glide.with(context)
                        .load(R.drawable.ic_product_barcode)
                        .into(barcodeImageView)
            }

            barcodeContentTextView.text = barcode.content
            barcodeTypeTextView.text = barcode.type
            createAtDateTextView.text = barcode.createAtDatetoString()
            createAtHoursTextView.text = barcode.createAtHourtoString()
        }
    }
}

interface IHistoryFragementListener {
    fun showCodeInformation(barcode: Barcode)
}