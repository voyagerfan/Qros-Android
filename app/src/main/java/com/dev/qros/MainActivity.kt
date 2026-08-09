package com.dev.qros

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.dev.qros.ui.screens.QrosMainScreen
import com.dev.qros.ui.screens.cameraPermission.CameraPermissionGateway
import com.dev.qros.ui.theme.QrosTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val qrosViewModel: QrosViewModel by viewModels()
        setContent {
            QrosTheme {
                CameraPermissionGateway {
                    QrosMainScreen(qrosViewModel)
                }
            }
        }
    }
}