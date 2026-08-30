package com.dev.qros.model

sealed interface QrosBarcode {
    val key: Int
    val isActioned: Boolean

    data class Contact(
        val contactInfo: ContactInfo,
        override val key: Int,
        override val isActioned: Boolean = false
    ) : QrosBarcode

    data class Url(
        val url: String,
        override val key: Int,
        override val isActioned: Boolean = false
    ) : QrosBarcode
}