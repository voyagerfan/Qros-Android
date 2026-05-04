package com.dev.qros.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo

@Entity
data class UrlData(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    @ColumnInfo val name: String,
    @ColumnInfo val description: String,
    @ColumnInfo val url: String
)

fun UrlData.mapToQrCodeData() = QrCodeData(
    id = this.uid,
    name = this.name,
    description = this.description,
    url = this.url
)

