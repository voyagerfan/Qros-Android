package com.dev.qros.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButtonMenu
import androidx.compose.material3.FloatingActionButtonMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.setValue
import com.dev.qros.model.QrosBarcode
import com.dev.qros.model.ScanState


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CameraScreenFab(
    scanState: ScanState
) {
    MaterialExpressiveTheme {
        var fabMenuExpanded by rememberSaveable { mutableStateOf(false) }

        MaterialExpressiveTheme {
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
                            onCheckedChange = { fabMenuExpanded = it }
                        ) {
                            // Morph icon smoothly based on the animation progress
                            val imageVector = if (checkedProgress > 0.5f) {
                                Icons.Default.Close
                            } else {
                                Icons.Default.Add
                            }
                            Icon(
                                painter = rememberVectorPainter(imageVector),
                                contentDescription = "Toggle menu"
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
}


@Preview
@Composable
fun PreviewCameraScreenFab() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        val emptyMockList: List<QrosBarcode> = emptyList()
        CameraScreenFab(
            scanState = ScanState.Success(data = emptyMockList)
        )
    }
}