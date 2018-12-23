package com.sag0ld.barcodegenerator.data

import android.arch.lifecycle.LiveData
import com.sag0ld.barcodegenerator.ui.base.App
import com.sag0ld.barcodegenerator.domain.models.Code
import org.jetbrains.anko.doAsync

class RoomRepository: CodeDao {

    override fun getCodeById(codeId: Long): LiveData<Code> {
        return AppDatabase.getAppDatabase(App.instance.applicationContext).codeDao().getCodeById(codeId)
    }

    override fun getAll(): LiveData<List<Code>> {
        return AppDatabase.getAppDatabase(App.instance.applicationContext).codeDao().getAll()
    }

    override fun insert(code: Code) {
        doAsync {
            AppDatabase.getAppDatabase(App.instance.applicationContext).codeDao().insert(code)
        }
    }

    override fun delete(code: Code) {
        doAsync {
            AppDatabase.getAppDatabase(App.instance.applicationContext).codeDao().delete(code)
        }
    }
}