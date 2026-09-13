package com.dev.qros.ui



import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.dev.qros.R
import com.dev.qros.model.QrosBarcode


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomSheetScanCta(
    barcodeList: List<QrosBarcode>,
    sheetState: SheetState,
    onActionClick: (isPositive: Boolean, barcode: QrosBarcode) -> Unit,
    onDismiss: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End
    ) {
        IconButton(
           onClick = { onDismiss() }
        ) {
            Icon(
                painter = painterResource(R.drawable.outline_close_24),
                contentDescription = null
            )
        }
    }
    ModalBottomSheet(
        onDismissRequest = { onDismiss() },
        sheetState = sheetState,
        sheetGesturesEnabled = false
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxWidth()
        ) {
            items(barcodeList.size) { index ->
                CtaRowItem(
                    barcode = barcodeList[index],
                    onActionClicked = { isPositive, barcode ->
                        onActionClick(isPositive, barcode)
                    }
                )
            }
        }
    }
}

@Composable
fun CtaRowItem(
    barcode: QrosBarcode,
    onActionClicked: (isPositive: Boolean, barcode: QrosBarcode) -> Unit
) {
    val displayText = when(barcode) {
        is QrosBarcode.Contact -> "Add New Contact: ${barcode.contactInfo.fullName}"
        is QrosBarcode.Url -> "Open URL: ${barcode.url}"
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ){
        Row {
            Text(text = displayText)
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ){
            if (barcode.isActioned) {
                Icon(
                    painter = painterResource(R.drawable.outline_check_24),
                    contentDescription = null
                )
            } else {
                IconButton(
                    onClick = { onActionClicked(false, barcode) }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.outline_close_24),
                        contentDescription = null
                    )
                }
                IconButton(
                    onClick = { onActionClicked(true, barcode) }
                ) {
                    Icon(
                        painter = painterResource(R.drawable.outline_close_24),
                        contentDescription = null
                    )
                }
            }
        }
    }
}