package com.dev.qros.model

data class CameraScreenState(
    val isScanningEnabled: Boolean = true,
    val shouldShowBottomDialogCta: Boolean = false,
    val currentQrosBarcodeList: List<QrosBarcode>, // TODO: potentially remoted and use ScanState
    val scanState: ScanState
)

sealed interface ScanState {
    data object Scanning: ScanState
    data class Success(val data: List<QrosBarcode> ): ScanState
    data class Error(val error: Throwable): ScanState
}
