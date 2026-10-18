package com.dev.qros.ui.screens.camera.camera

import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.dev.qros.model.ScanState
import java.util.concurrent.Executors


@OptIn(ExperimentalGetImage::class)
@Composable
fun CameraScreen(
    modifier: Modifier = Modifier,
    scanState: ScanState,
    fab: @Composable () -> Unit,
    onProcessImage: (ImageProxy) -> Unit,
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
    var imageAnalyzerRef by remember { mutableStateOf<ImageAnalysis?>(null) }

    Scaffold(
        modifier = modifier,
        floatingActionButton = fab
    ) { innerPadding ->

        LaunchedEffect(scanState, imageAnalyzerRef) {
            if (scanState !is ScanState.Scanning) {
                // Instantly stop ML Kit processing at the CameraX pipeline level
                imageAnalyzerRef?.clearAnalyzer()

                Log.d("CameraScreen", "Analyzer cleared - CameraX pipeline paused.")
            } else {
                imageAnalyzerRef?.setAnalyzer(cameraExecutor) { imageProxy ->
                    onProcessImage(imageProxy)
                }
                Log.d("CameraScreen", "Analyzer active - streaming frames.")
            }
        }

        AndroidView(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            factory = { ctx ->
                val previewView = PreviewView(ctx)
                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

                cameraProviderFuture.addListener({
                    val cameraProvider = cameraProviderFuture.get()

                    //Camera Preview
                    val preview = Preview.Builder().build().apply {
                        surfaceProvider = previewView.surfaceProvider
                    }

                    //Image Analysis (ML Kit integration)
                    val imageAnalyzer = ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()

                    imageAnalyzerRef = imageAnalyzer

                    try {
                        cameraProvider.unbindAll()
                        // Bind camera feed and analysis  to the Compose lifecycle
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            CameraSelector.DEFAULT_BACK_CAMERA,
                            preview,
                            imageAnalyzer
                        )
                    } catch (e: Exception) {
                        Log.e("CameraPreview", "Use case binding failed", e)
                    }
                }, ContextCompat.getMainExecutor(ctx))

                previewView
            }
        )
    }
}