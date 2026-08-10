package com.dev.qros.ui.screens.camera.camera

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

@Composable
fun CameraPermissionGateway(
    appContent: @Composable () -> Unit
) {
    val context = LocalContext.current
    // 1. Keep track of whether we need to display our custom rationale dialog
    var showRationaleDialog by remember { mutableStateOf(false) }
    var showAppContent: Boolean? by remember { mutableStateOf(null) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { showAppContent = it }

    // Helper function to execute your exact step-by-step logic
    val checkAndRequestPermission = {
        val activity = context as? ComponentActivity

        when {
            // Already Granted
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED -> {
                showAppContent = true
            }

            // User previously denied it, but hasn't checked "Don't ask again"
            activity != null && ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.CAMERA) -> {
                showRationaleDialog = true
            }

            // First-time request
            else -> {
                permissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    // Trigger the check immediately when this screen becomes visible
    LaunchedEffect(Unit) {
        checkAndRequestPermission()
    }

    if(showAppContent != null) {
        appContent()
    }

    if (showRationaleDialog) {
        AlertDialog(
            onDismissRequest = { showRationaleDialog = false },
            title = { Text("Camera Access Required") },
            text = { Text("We need camera access so you can instantly scan attendee QR codes at this event.") },
            confirmButton = {
                Button(onClick = {
                    showRationaleDialog = false
                    permissionLauncher.launch(Manifest.permission.CAMERA)
                }) {
                    Text("Allow")
                }
            },
            dismissButton = {
                Button(onClick = {
                    showRationaleDialog = false
                    showAppContent = false
                }) {
                    Text("Cancel")
                }
            }
        )
    }
}