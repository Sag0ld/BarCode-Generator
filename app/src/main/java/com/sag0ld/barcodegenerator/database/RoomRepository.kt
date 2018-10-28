package com.sag0ld.barcodegenerator.database

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
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
        doAsync {
            AppDatabase.getAppDatabase(App.instance.applicationContext).barcodeDao().delete(barcode)
        }
    }
}