package com.dev.qros.model

import android.graphics.Bitmap

data class QrCodeData(
    val id: Int,
    val name: String,
    val description: String,
    val url: String,
    val qrCode: Bitmap? = null
)

fun QrCodeData.getUrl(): String {
    return if (url.contains("BEGIN:VCARD")) "V-Card" else url
}

