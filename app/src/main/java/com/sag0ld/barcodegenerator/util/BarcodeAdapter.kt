package com.sag0ld.barcodegenerator.util

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.view.LayoutInflater
import java.util.*
import android.widget.RelativeLayout
import com.sag0ld.barcodegenerator.R
import com.sag0ld.barcodegenerator.domain.models.Code


class BarcodeAdapter(val context: Context): RecyclerView.Adapter<BarcodeAdapter.CodeViewHolder>() {

    var listener: IHistoryFragementListener? = null

    var codes = ArrayList<Code>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CodeViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_barcode, parent, false)
        return CodeViewHolder(view)
    }

    override fun getItemCount(): Int {
        return codes.size
    }

    override fun onBindViewHolder(holder: CodeViewHolder, position: Int) {
        holder.bind(codes[position])
        holder.view.setOnClickListener {
            listener?.showCodeInformation(codes[position])
        }
        holder.view.setOnLongClickListener {
            listener?.deleteCode(codes[position])

            true
        }
    }

    fun removeItem(adapterPosition: Int) {
        codes.removeAt(adapterPosition)
        notifyItemRemoved(adapterPosition)
    }

    fun restoreItem(deletedItem: Code, deletedIndex: Int) {
        codes.add(deletedIndex, deletedItem)
        listener?.addCode(deletedItem)
        notifyDataSetChanged()
    }

    inner class CodeViewHolder(val view: View): RecyclerView.ViewHolder(view) {
        val viewBackground = view.findViewById<RelativeLayout>(R.id.view_background)
        val viewForeground = view.findViewById<RelativeLayout>(R.id.view_foreground)
        val barcodeImageView = view.findViewById<ImageView>(R.id.barcodeImageView)
        val barcodeTypeTextView = view.findViewById<TextView>(R.id.barcodeTypeTextView)
        val barcodeContentTextView = view.findViewById<TextView>(R.id.barcodeContentTextView)
        val createAtDateTextView = view.findViewById<TextView>(R.id.createAtDateTextView)
        val createAtHoursTextView = view.findViewById<TextView>(R.id.createAtHoursTextView)

        fun bind(code: Code) {
            if (code.isQrCode())
                barcodeImageView.setImageResource(R.drawable.ic_qr_code)
            else
                barcodeImageView.setImageResource(R.drawable.ic_product_barcode)

            barcodeTypeTextView.text = code.type
            barcodeContentTextView.text = code.content
            createAtDateTextView.text = code.createAtDatetoString()
            createAtHoursTextView.text = code.createAtHourtoString()
        }
    }
}

interface IHistoryFragementListener {
    fun showCodeInformation(code: Code)
    fun deleteCode(code: Code)
    fun addCode(deletedItem: Code) {
    }
}