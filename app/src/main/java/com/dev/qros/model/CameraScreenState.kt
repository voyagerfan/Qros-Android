package com.dev.qros.model

data class CameraScreenState(
    val isScanningEnabled: Boolean = true,
    val shouldShowUrlDialog: Boolean = false,
    val shouldShowContactsDialog: Boolean = false,
    val url: String
)
