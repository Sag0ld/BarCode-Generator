package com.sag0ld.barcodegenerator.data

import android.arch.lifecycle.LiveData
import com.sag0ld.barcodegenerator.data.base.App
import com.sag0ld.barcodegenerator.domain.Barcode
import org.jetbrains.anko.doAsync

class RoomRepository: BarcodeDao {

    override fun getBarcodeById(barcodeId: Long): LiveData<Barcode> {
        return AppDatabase.getAppDatabase(App.instance.applicationContext).barcodeDao().getBarcodeById(barcodeId)
    }

    override fun getAll(): LiveData<List<Barcode>> {
        return AppDatabase.getAppDatabase(App.instance.applicationContext).barcodeDao().getAll()
    }

    override fun insert(barcode: Barcode) {
        doAsync {
            AppDatabase.getAppDatabase(App.instance.applicationContext).barcodeDao().insert(barcode)
        }
    }

    override fun delete(barcode: Barcode) {
        doAsync {
            AppDatabase.getAppDatabase(App.instance.applicationContext).barcodeDao().delete(barcode)
        }
    }
}