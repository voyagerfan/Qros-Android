package com.dev.qros.model

data class VCardData(
    val fullName: String,
    val phone: String? = null,
    val email: String? = null,
    val company: String? = null,
    val jobTitle: String? = null,
    val url: String? = null,
)

fun VCardData.toVCardString(): String {
    return buildString() {
        appendLine("BEGIN:VCARD")
        appendLine("VERSION:3.0")
        appendLine("FN:${fullName}")

        if(!phone.isNullOrBlank()) appendLine("TEL;TYPE=cell:${phone}")
        if(!email.isNullOrBlank()) appendLine("EMAIL:${email}")
        if(!company.isNullOrBlank()) appendLine("ORG:${company}")
        if(!jobTitle.isNullOrBlank()) appendLine("TITLE:${jobTitle}")
        if(!url.isNullOrBlank()) appendLine("URL:${url}")

        append("END:VCARD")
    }
}
