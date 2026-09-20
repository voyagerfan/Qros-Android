package com.dev.qros.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.CropFree
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Share
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
import com.dev.qros.model.ScanState


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CameraScreenFab(
    scanState: ScanState
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
                // Option 1
                FloatingActionButtonMenuItem(
                    onClick = { fabMenuExpanded = false },
                    icon = { Icon(Icons.Default.Create, contentDescription = null) },
                    text = { Text("Create New") }
                )
                // Option 2
                FloatingActionButtonMenuItem(
                    onClick = { fabMenuExpanded = false },
                    icon = { Icon(Icons.Default.Favorite, contentDescription = null) },
                    text = { Text("Favorite") }
                )
                // Option 3
                FloatingActionButtonMenuItem(
                    onClick = { fabMenuExpanded = false },
                    icon = { Icon(Icons.Default.Share, contentDescription = null) },
                    text = { Text("Share Item") }
                )
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


@Preview
@Composable
fun PreviewCameraScreenFab() {
    // Testing

    val emptyMockList: List<QrosBarcode> = emptyList()
    val throwable = Throwable()
    CameraScreenFab(
        scanState = ScanState.Ready
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