package com.dev.qros.ui.components

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CropFree
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButtonMenu
import androidx.compose.material3.FloatingActionButtonMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dev.qros.model.QrosBarcode
import com.dev.qros.model.ScanMenuAction
import com.dev.qros.model.ScanMenuConfig
import com.dev.qros.model.ScanState


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CameraScreenFab(
    scanState: ScanState,
    onFabMenuSelected: (ScanMenuConfig) -> Unit
) {
    MaterialExpressiveTheme {
        var fabMenuExpanded by rememberSaveable { mutableStateOf(false) }
        Box(
            modifier = Modifier.fillMaxSize()
        ) {
            FloatingActionButtonMenu(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp),
                expanded = fabMenuExpanded,
                button = {
                    ToggleFloatingActionButton(
                        checked = fabMenuExpanded,
                        onCheckedChange = { fabMenuExpanded = it },
                    ) {
                        ToggleContent(
                            scanState = scanState,
                            checkedProgress = checkedProgress
                        )
                    }
                }
            ) {
                scanMenuContentData(scanState = scanState)
                    .forEach { config ->
                        FloatingActionButtonMenuItem(
                            onClick = {
                                fabMenuExpanded = false
                                onFabMenuSelected(config)
                            },
                            icon = { Icon(config.icon, contentDescription = null) },
                            text = { Text(text = config.label) }
                        )
                    }

            }
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ToggleContent(
    scanState: ScanState,
    checkedProgress: Float,
) {

    val iconVector = when (scanState) {
        is ScanState.Scanning -> null
        is ScanState.Ready -> Icons.Default.CropFree
        is ScanState.Error -> Icons.Default.Warning
        is ScanState.Success -> Icons.Default.CheckCircle
    }

    /*when (scanState) {
        is ScanState.Scanning -> {
            Log.d("scanState", "scanState is scanning")
        }
        is ScanState.Ready -> {
            Log.d("scanState", "scanState is ready")
        }
        is ScanState.Error -> {
            Log.d("scanState", "scanState is error")}
        is ScanState.Success -> {
            Log.d("scanState", "scanState is success")}
    }*/

    if (checkedProgress > 0.5f) {
        Icon(
            painter = rememberVectorPainter(Icons.Default.Close),
            contentDescription = "Toggle menu"
        )
    } else {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.wrapContentSize()
        ) {
            iconVector?.let { vector ->
                Icon(
                    painter = rememberVectorPainter(vector),
                    contentDescription = "Toggle menu"
                )
            } ?: ContainedLoadingIndicator()
        }
    }
}

fun scanMenuContentData(
    scanState: ScanState,
): List<ScanMenuConfig> {
    return when(scanState) {
        is ScanState.Error -> listOf(
                ScanMenuConfig(
                action = ScanMenuAction.ERROR_CANCEL,
                icon = Icons.Default.Close,
                label = "Cancel",
            ),
            ScanMenuConfig(
                action = ScanMenuAction.ERROR_RETRY,
                icon = Icons.Default.Refresh,
                label = "Try again",

            )
        )
        is ScanState.Scanning -> listOf(
            ScanMenuConfig(
                action = ScanMenuAction.SCANNING_STOP,
                icon = Icons.Default.Stop,
                label = "Stop Scanning",
            )
        )
        is ScanState.Ready -> listOf(
            ScanMenuConfig(
                action = ScanMenuAction.IDLE_START_SCAN,
                icon = Icons.Default.PlayArrow,
                label = "Start Scanning",
            ),
            ScanMenuConfig(
                action = ScanMenuAction.IDLE_TO_RECENT_SCANS,
                icon = Icons.AutoMirrored.Filled.List,
                label = "See recent scans",
            )
        )
        is ScanState.Success -> listOf(

            ScanMenuConfig(
                action = ScanMenuAction.SUCCESS_DEPLOY_SHEET,
                icon = Icons.Default.KeyboardArrowUp,
                label = "See Current Scans",
            ),
            ScanMenuConfig(
                action = ScanMenuAction.SUCCESS_CONTINUE_SCANNING,
                icon = Icons.Default.Add,
                label = "Continue Scanning",
            ),
            ScanMenuConfig(
                action = ScanMenuAction.SUCCESS_CANCEL,
                icon = Icons.Default.Close,
                label = "Im Done",
            )
        )
    }
}


@Preview
@Composable
fun PreviewCameraScreenFab() {

    val emptyMockList: List<QrosBarcode> = emptyList()
    val throwable = Throwable()
    CameraScreenFab(
        scanState = ScanState.Error(throwable),
        onFabMenuSelected = {}
    )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Preview
@Composable
fun PreviewLoadingIndicator() {
    // testing
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        ContainedLoadingIndicator()
        LoadingIndicator()

        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onErrorContainer
        )
    }
}