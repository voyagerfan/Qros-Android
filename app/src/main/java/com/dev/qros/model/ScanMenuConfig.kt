package com.dev.qros.model

import androidx.compose.ui.graphics.vector.ImageVector

data class ScanMenuConfig(
    val action: ScanMenuAction,
    val icon: ImageVector,
    val label: String
)
