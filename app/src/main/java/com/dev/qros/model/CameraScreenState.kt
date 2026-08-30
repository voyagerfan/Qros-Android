package com.dev.qros.model

data class CameraScreenState(
    val isScanningEnabled: Boolean = true,
    val shouldShowBottomDialogCta: Boolean = false,
    val currentQrosBarcodeList: List<QrosBarcode>
)
