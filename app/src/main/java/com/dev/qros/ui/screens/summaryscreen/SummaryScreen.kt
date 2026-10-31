package com.dev.qros.ui.screens.summaryscreen

import android.annotation.SuppressLint
import android.util.Log
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.CropFree
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Storage
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.FloatingToolbarHorizontalFabPosition
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleFloatingActionButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.dev.qros.model.QrosUiState
import com.dev.qros.model.Scan
import com.dev.qros.model.ScanType

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SummaryScreen(
    scanSummaryState: QrosUiState<List<Scan>>,
    retryOnError: () -> Unit = {},
    onItemLongPress: () -> Unit = {}
) {
    //TODO: Consider "my scans, recent scans, favorites, all scans"
    Scaffold(

    ) {
        when (scanSummaryState) {
            is QrosUiState.Loading -> {
                SummaryLoadingScreen()
            }
            is QrosUiState.Success -> {
                // TODO: design iterable item then add to a list
                // TODO: Consider adding a favorites feature to the list item
            }
            is QrosUiState.Empty -> {
                SummaryEmptyScreen()
            }
            is QrosUiState.Error -> {
                Log.d("Summary View Error: ", "${scanSummaryState.error}")
                SummaryScreenError(
                    onRetry = retryOnError
                )
            }
        }
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

/**
 * Creates the ScanItem Composable.
 *
 * Formats and displays a single scan item.
 *
 * @param Scan The Scan object from the "scans" DB.
 * @param OnLongPress action to take when a scan Item is long pressed - trigger delete checkboxes.
 * @param shouldShowCheckbox checkbox to mark for deletion. Show on long press
 * @param onItemChecked action to take when a checkbox is checked. Provides the uid of the item and the checked state.
 * @return The ScanItem Composable.
 */
@Composable
fun ScanItem(
    scan: Scan,
    onLongPress: () -> Unit,
    shouldShowCheckbox: Boolean = false,
    onItemChecked: (Boolean, Int) -> Unit = { _, _ -> }
){
    var checked by rememberSaveable { mutableStateOf(false) }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = {},
                onLongClick = {
                    // disable the long press if the checkbox is showing
                    if (!shouldShowCheckbox) {
                        onLongPress()
                        checked = !checked
                        onItemChecked(true, scan.uid)
                    }
                }
            )
    ) {
        if (shouldShowCheckbox) {
            Checkbox(
                checked = checked,
                onCheckedChange = {
                    checked = it
                    onItemChecked(it, scan.uid)
                },
            )
        }
        IconForScanType(scan.type)

        Spacer(modifier = Modifier.width(24.dp))

        Column(
            modifier = Modifier.wrapContentHeight()
        ) {
            Row {
                Text(text = scanPrefix(scan.type))
                Text(text = scan.payload ?: "")
            }
            Text(text = scan.getFormattedTimestamp())
        }
    }
}

@Composable
fun IconForScanType(scanType: ScanType) {
    return when (scanType) {
        ScanType.URL -> {
            Icon(
                imageVector = Icons.Outlined.Link,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
        ScanType.CONTACT_INFO -> {
            Icon(
                imageVector = Icons.Outlined.Person,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
        ScanType.UNKNOWN -> {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.HelpOutline,
                contentDescription = null,
                modifier = Modifier.size(40.dp),
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

fun scanPrefix( scanType: ScanType): String {
    return when (scanType) {
        ScanType.URL -> "URL: "
        ScanType.CONTACT_INFO -> "Contact: "
        ScanType.UNKNOWN -> "Unknown"
    }
}

fun onItemClickedHandler(scan: Scan) {
    when (scan.type) {
        ScanType.URL -> {
            // open a browser
        }
        ScanType.CONTACT_INFO -> {
            // Start the contact activity
        }
        ScanType.UNKNOWN -> {
            // no action
        }
    }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SummaryLoadingScreen() {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        LoadingIndicator(
            modifier = Modifier.size(96.dp)
        )
    }
}

@Composable
fun SummaryEmptyScreen(
    onNewScanClicked: () -> Unit = {}
) {
    MaterialExpressiveTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                modifier = Modifier.size(96.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.CropFree,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "No scans yet",
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Items added to your collection will show up right here. Get started by creating your first one.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(
                onClick = onNewScanClicked
            ) {
                Text("Add New Scan")
            }
        }
    }
}

@Composable
fun SummaryScreenError(
    onRetry: () -> Unit = {}
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.errorContainer,
            modifier = Modifier.size(96.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Storage,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))


        Text(
            text = "Something went wrong",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "We couldn't load your data right now.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Button(onClick = onRetry) {
                Icon(
                    imageVector = Icons.Outlined.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Try Again")
            }
        }
    }
}

@Preview
@Composable
fun PreviewSummaryLoadingScreen() {
    SummaryLoadingScreen()
}

@Preview
@Composable
fun PreviewSummaryEmptyScreen() {
    SummaryEmptyScreen()
}

@Preview
@Composable
fun PreviewSummaryScreenError() {
    SummaryScreenError()
}

@Preview
@Composable
fun PreviewSummaryScreenTopBar() {
    SummaryScreenTopBar()
}

@Preview
@Composable
fun PreviewScanItemUrl() {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize()
    ) {
        ScanItem(
            scan = Scan(
                type = ScanType.URL,
                payload = "https://google.com",
                timestamp = System.currentTimeMillis()
            ),
            onLongPress = {},
            shouldShowCheckbox = true
        )
    }
}

@Preview
@Composable
fun PreviewSummaryScreen() {
    //SummaryScreen()
}