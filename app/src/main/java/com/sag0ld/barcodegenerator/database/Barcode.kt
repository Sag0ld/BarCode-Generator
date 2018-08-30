package com.sag0ld.barcodegenerator.database

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.util.*

@Entity(tableName = "barcodes")
class Barcode {
    @PrimaryKey
    var uid: Int = 0

    @ColumnInfo(name = "content")
    var content: String? = null

    @ColumnInfo(name = "uri")
    var uri: String? = null

    @ColumnInfo(name = "create_at")
    var createAt: Long? = null
}