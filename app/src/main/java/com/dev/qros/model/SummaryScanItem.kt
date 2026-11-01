package com.dev.qros.model

data class SummaryScanItem(
    val scanInfo: Scan,
    val wasLongPressed: Boolean = false,
    val isChecked: Boolean = false
)
