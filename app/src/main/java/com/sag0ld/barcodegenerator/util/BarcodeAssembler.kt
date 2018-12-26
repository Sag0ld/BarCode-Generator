package com.sag0ld.barcodegenerator.util

import com.sag0ld.barcodegenerator.data.barcodes.AbstractBarcode
import com.sag0ld.barcodegenerator.domain.Barcode

class BarcodeAssembler {
    companion object {
        fun toBarcode(content: String, type: String): Barcode {
            return Barcode(content, type)
        }

        fun toAbstractBarcode(barcode: Barcode): AbstractBarcode? {
            barcode.type?.let { type ->
                var abstractBarcode = BarcodeFactory.instance.createBarcodeEntity(type)
                barcode.content?.let { content ->
                    abstractBarcode.content = content
                    barcode.createAt?.let { time ->
                        abstractBarcode.createAt.timeInMillis = time
                        return abstractBarcode
                    }
                }
            }
            return null
        }
    }
}