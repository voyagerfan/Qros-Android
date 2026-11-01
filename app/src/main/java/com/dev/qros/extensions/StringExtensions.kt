package com.dev.qros.extensions

fun String.extractUrl(): String {
    val urlAnchorPattern = Regex("URL:", RegexOption.IGNORE_CASE)
    val match = urlAnchorPattern.find(this)

    return if (match != null) {
        // handle MEBKM format
        // example -> MEBKM:TITLE:Google;URL:https://www.google.com;;
        this.substring(match.range.last + 1).trim(';')
    } else {
        this
    }
}