package com.sag0ld.barcodegenerator.database

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity(tableName = "barcodes")
class Barcode {
    @PrimaryKey
    private val uid: Int = 0

    @ColumnInfo(name = "content")
    private val content: String? = null

    @ColumnInfo(name = "uri")
    private val uri: String? = null

    @ColumnInfo(name = "create_at")
    private val create_at: String? = null
}