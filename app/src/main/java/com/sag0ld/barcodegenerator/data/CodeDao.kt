package com.sag0ld.barcodegenerator.data

import android.arch.lifecycle.LiveData
import android.arch.persistence.room.Dao
import android.arch.persistence.room.Delete
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import com.sag0ld.barcodegenerator.domain.models.Code

@Dao
interface CodeDao {
    @Query("SELECT * FROM codes")
    fun getAll(): LiveData<List<Code>>

    @Query("SELECT * FROM codes WHERE id = :codeId")
    fun getCodeById(codeId: Long): LiveData<Code>

    @Insert
    fun insert(code: Code)

    @Delete
    fun delete(code: Code)
}