package com.dev.qros.extensions

import com.dev.qros.model.Scan
import com.dev.qros.model.ScanType
import com.google.mlkit.vision.barcode.common.Barcode

fun Barcode.toScan(type: Int, payload: String?): Scan {
    return Scan(
        type = type.scanTypeToEnum(),
        payload = payload,
        timestamp = System.currentTimeMillis()
    )
}

fun Int.scanTypeToEnum(): ScanType {
    return when(this) {
        1 -> {
            ScanType.CONTACT_INFO
        }
        8 -> {
            ScanType.URL
        }
        else -> {
            ScanType.UNKNOWN
        }
    }
}