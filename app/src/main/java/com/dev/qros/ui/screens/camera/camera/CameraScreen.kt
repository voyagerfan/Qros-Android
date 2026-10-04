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
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import java.util.concurrent.Executors


@OptIn(ExperimentalGetImage::class)
@Composable
fun CameraScreen(
    modifier: Modifier = Modifier,
    fab: @Composable () -> Unit,
    onProcessImage: (ImageProxy) -> Unit,
) {

    /** TODO: Test terminating imageProxy at the source. Keep defensive guard in viewmodel to divert imageProxy
     * val lifecycleOwner = LocalLifecycleOwner.current
     *
     *     // Hold a mutable reference to the analyzer so we can clear/re-set it externally
     *     var imageAnalyzerRef by remember { mutableStateOf<ImageAnalysis?>(null) }
     *     val cameraExecutor = remember { Executors.newSingleThreadExecutor() }
     *
     *     // React to state changes!
     *     LaunchedEffect(scanState) {
     *         if (scanState !is ScanState.Scanning) {
     *             // Instantly stop ML Kit processing at the CameraX pipeline level
     *             imageAnalyzerRef?.clearAnalyzer()
     *             Log.d("CameraScreen", "Analyzer cleared - CameraX pipeline paused.")
     *         } else {
     *             // Re-enable the analyzer when we go back to scanning
     *             imageAnalyzerRef?.setAnalyzer(cameraExecutor) { imageProxy ->
     *                 onProcessImage(imageProxy)
     *             }
     *             Log.d("CameraScreen", "Analyzer active - streaming frames.")
     *         }
     *     }
     */

    val lifecycleOwner = LocalLifecycleOwner.current
    Scaffold(
        modifier = modifier,
        floatingActionButton = fab
    ) { innerPadding ->
        AndroidView(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            factory = { ctx ->
                val previewView = PreviewView(ctx)
                val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)
                val cameraExecutor = Executors.newSingleThreadExecutor()

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

                    /* TODO: enable this and test after code at top is vetted
                    * if (scanState is ScanState.Scanning) {
                        imageAnalyzer.setAnalyzer(cameraExecutor) { imageProxy ->
                            onProcessImage(imageProxy)
                        }
                    }
                    * */

                    imageAnalyzer.setAnalyzer(
                        cameraExecutor
                    ) { imageProxy -> onProcessImage(imageProxy) }

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