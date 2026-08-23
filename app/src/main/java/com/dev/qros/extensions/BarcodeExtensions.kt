package com.dev.qros.extensions

import com.dev.qros.model.ContactInfo
import com.dev.qros.model.Scan
import com.dev.qros.model.ScanType
import com.google.mlkit.vision.barcode.common.Barcode

fun Barcode.toScan(): Scan {
    return Scan(
        type = valueType.scanTypeToEnum(),
        payload = rawValue,
        timestamp = System.currentTimeMillis()
    )
}

/* TODO: create mapper to a ContactInfo Object

fun Barcode.toContactInfo(): ContactInfo {
    val contactInfo = this.contactInfo
    return ContactInfo(
        fullName = contactInfo?.name ?: "",
        phone = contactInfo?.phones ?: "",
        email = email ?: "",
        company = organization ?: "",
        jobTitle = title ?: "",
        url = url ?: ""
    )
}*/

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