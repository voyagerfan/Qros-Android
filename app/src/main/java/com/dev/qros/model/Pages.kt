package com.dev.qros.model

import com.dev.qros.R


enum class Pages(val route: String, val icon: Int, val contentDescription: String) {
    QR_CODE_GRAPH(
        route = "qr_code_graph",
        icon = R.drawable.outline_qr_code_2_24,
        contentDescription = "qr code screen"),
    CAMERA_GRAPH(
        route = "camera_graph",
        icon = R.drawable.outline_photo_camera_24,
        "camera screen"
    ),
    FILE_GRAPH(
        route = "file_graph",
        icon = R.drawable.outline_folder_24,
        contentDescription = "file screen"
    ),
    SETTINGS_GRAPH(
        route = "settings_graph",
        icon = R.drawable.outline_settings_24,
        contentDescription = "settings screen"
    ),
}

enum class QrCodeSubGraph {
    HOME,
    PROMPT_SCREEN,
    NEW_V_CARD_SCREEN,
    NEW_QR_CODE_SCREEN
}

enum class CameraSubGraph {
    CAMERA_VIEW,
    ACTIONS
}
