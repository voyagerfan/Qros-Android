package com.dev.qros.ui.screens.summaryscreen

import android.annotation.SuppressLint
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.FloatingToolbarHorizontalFabPosition
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SummaryScreen() {
    Scaffold(

    ) {

    }
}
@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SummaryScreenTopBar() {
    val vibrantColors = FloatingToolbarDefaults.vibrantFloatingToolbarColors()
    var isExpanded by rememberSaveable { mutableStateOf(false) }
    var query by remember { mutableStateOf("") }

    MaterialExpressiveTheme {
        HorizontalFloatingToolbar(
            expanded = isExpanded,
            floatingActionButtonPosition = FloatingToolbarHorizontalFabPosition.Start,
            floatingActionButton = {
                ToggleFloatingActionButton(
                    checked = isExpanded,
                    onCheckedChange = { isExpanded = it },
                ) {
                    val iconVector by remember {
                        derivedStateOf {
                            if (checkedProgress > 0.5f) Icons.Default.Close else Icons.Default.Search
                        }
                    }
                    Icon(imageVector = iconVector, contentDescription = null)
                }
            },
            colors = vibrantColors,
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                placeholder = { Text("Search...") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                )
            )
        }
    }
}

@Preview
@Composable
fun PreviewSummaryScreenTopBar() {
    SummaryScreenTopBar()
}

@Preview
@Composable
fun PreviewSummaryScreen() {
    SummaryScreen()
}