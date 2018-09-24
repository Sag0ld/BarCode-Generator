package com.sag0ld.barcodegenerator.database

import android.arch.lifecycle.LiveData
import com.sag0ld.barcodegenerator.App
import org.jetbrains.anko.doAsync

class RoomRepository: BarcodeDao {

    override fun getAll(): LiveData<List<Barcode>> {
        return AppDatabase.getAppDatabase(App.instance.applicationContext).barcodeDao().getAll()
    }

    override fun insert(barcode: Barcode) {
        doAsync {
            AppDatabase.getAppDatabase(App.instance.applicationContext).barcodeDao().insert(barcode)
        }
    }

    override fun delete(barcode: Barcode) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}