package com.dev.qros.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scans")
data class Scan(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    @ColumnInfo val type: ScanType,
    @ColumnInfo val payload: String? = null,
    @ColumnInfo val timestamp: Long
)

