package com.sag0ld.barcodegenerator

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.sag0ld.barcodegenerator.barcodes.Barcode

class BarcodeAdapter(val context: Context): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    var barcodes = mutableListOf<Barcode>()
        set(value) {
            barcodes = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return BarcodeViewHolder(parent)
    }

    override fun getItemCount(): Int {
        return barcodes.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as BarcodeViewHolder).bind(barcodes[position])
    }

    inner class BarcodeViewHolder(view: View): RecyclerView.ViewHolder(view) {

        val barcodeImageView = view.findViewById<ImageView>(R.id.barcodeImageView)
        val barcodeContentTextView = view.findViewById<TextView>(R.id.barcodeContentTextView)
        val createAtTextView = view.findViewById<TextView>(R.id.createAtTextView)

        fun bind(barcode: Barcode) {
            Glide.with(context)
                    .load(barcode.generate())
                    .into(barcodeImageView)
            barcodeContentTextView.text = barcode.content
            createAtTextView.text = barcode.createAt?.time?.toString()
        }
    }
}