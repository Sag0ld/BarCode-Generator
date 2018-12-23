package com.sag0ld.barcodegenerator.ui.viewModels

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.graphics.Bitmap
import com.sag0ld.barcodegenerator.domain.models.Code
import com.sag0ld.barcodegenerator.data.RoomRepository

class CodeDetailsViewModel: ViewModel() {

    private val repository =  RoomRepository()
    var bitmapLiveData = MutableLiveData<Bitmap>()
    lateinit var codeLiveData: LiveData<Code>

    fun getBarcode(codeId: Long): LiveData<Code> {
        return repository.getCodeById(codeId)
    }

    fun deleteBarcode(code: Code) {
        repository.delete(code)
    }
}