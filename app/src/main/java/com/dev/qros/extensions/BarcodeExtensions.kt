package com.dev.qros.extensions

import com.dev.qros.model.ContactInfo
import com.dev.qros.model.ContactNumbers
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

fun Barcode.toContactInfo(): ContactInfo {
    val contactInfo = this.contactInfo
    val faxNumbers = mutableListOf<String?>()
    val phoneNumbers = mutableListOf<String?>()
    val contactEmails = mutableListOf<String?>()

    // parse the numbers separating fax from everything else
    contactInfo?.phones?.forEach { phone ->
        when(phone.type) {
            Barcode.Phone.TYPE_FAX -> faxNumbers.add(phone.number)
            else -> phoneNumbers.add(phone.number)
        }
    }

    // parse the emails addresses into a list
    contactInfo?.emails?.forEach { email ->
        contactEmails.add(email.address)
    }

    return ContactInfo(
        fullName = contactInfo?.name?.formattedName,
        phone = ContactNumbers(faxNums = faxNumbers, phoneNums = phoneNumbers),
        email = contactEmails,
        company = contactInfo?.organization,
        jobTitle = contactInfo?.title,
        url = contactInfo?.urls
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