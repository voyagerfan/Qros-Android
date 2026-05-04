package com.dev.qros.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.dev.qros.QrosViewModel
import com.dev.qros.model.Form
import com.dev.qros.model.QrCodeData
import com.dev.qros.model.QrosUiState
import com.dev.qros.model.UrlData
import com.dev.qros.model.VCardData
import com.dev.qros.model.getUrl
import com.dev.qros.model.toVCardString
import kotlinx.coroutines.launch

@Composable
fun QrosMainScreen(viewModel: QrosViewModel) {

    val qrosUiState by viewModel.qrosUiState.collectAsStateWithLifecycle()
    val vCardDataState by viewModel.vCardDataState.collectAsStateWithLifecycle()
    var showAddNewQrContent by rememberSaveable { mutableStateOf(false) }

    val scope = rememberCoroutineScope()

    QrosMainScreen(
        topBar = { QrosTopBar() },
        bottomBar = { QrosBottomBar()},
        floatingActionBtn = {
            if(!showAddNewQrContent) {
                AddQrCodeFloatingAction {
                    showAddNewQrContent = !showAddNewQrContent
                }
            }
        },
        onSuccessContent = { qrCodeData, padding ->
            if(showAddNewQrContent) {
                PromptScreen(
                    vCardDataState = vCardDataState,
                    onUpdateVCard = { vCard -> viewModel.updateVCardData(vCard)},
                    scaffoldPadding = padding,
                    onSave = { urlData ->
                        scope.launch { viewModel.addNewUrl(urlData) }
                        showAddNewQrContent = false
                    },
                    onCancel = { showAddNewQrContent = it }
                )
            } else {
                RenderQrCodes(
                    qrCodeDataList = qrCodeData,
                    scaffoldPadding = padding
                )
            }
        },
        onLoadingContent = { padding -> CustomLoadingIndicator(scaffoldPadding = padding) },
        onErrorContent = { throwable, padding ->
            ErrorScreen(padding, throwable)
        },
        onEmptyContent = { padding ->
            PromptScreen(
                vCardDataState = vCardDataState,
                onUpdateVCard = { vCard -> viewModel.updateVCardData(vCard) },
                scaffoldPadding = padding,
                onSave = { urlData ->
                    scope.launch { viewModel.addNewUrl(urlData) }
                },
                onCancel = { showAddNewQrContent = it }
            )
        },
        qrosUiState = qrosUiState
    )
}

@Composable
internal fun QrosMainScreen(
    topBar: @Composable () -> Unit,
    bottomBar: @Composable () -> Unit,
    floatingActionBtn: @Composable () -> Unit,
    onSuccessContent: @Composable (List<QrCodeData>, PaddingValues) -> Unit,
    onLoadingContent: @Composable (PaddingValues) -> Unit,
    onErrorContent: @Composable (Throwable, PaddingValues) -> Unit,
    onEmptyContent: @Composable (PaddingValues) -> Unit,
    qrosUiState: QrosUiState<List<QrCodeData>>
) {
    Scaffold(
        topBar = topBar,
        bottomBar = bottomBar,
        floatingActionButton = floatingActionBtn,
        floatingActionButtonPosition =  FabPosition.End,
    ) { innerPadding ->

        when(qrosUiState) {
            is QrosUiState.Loading -> {
                onLoadingContent(innerPadding)
            }
            is QrosUiState.Success -> {
                onSuccessContent(qrosUiState.data, innerPadding)
            }
            is QrosUiState.Empty -> {
                onEmptyContent(innerPadding)
            }
            is QrosUiState.Error -> {
                onErrorContent(qrosUiState.error, innerPadding)
            }
        }
    }
}

@Composable
fun QrosTopBar() {
    NavigationBar {}
}

@Composable
fun QrosBottomBar(
) {
    NavigationBar {
        Button(
            onClick = { }
        ) {
            Text("Delete QR Code")
        }
    }
}

@Composable
fun RenderQrCodes(
    qrCodeDataList: List<QrCodeData>,
    scaffoldPadding: PaddingValues,
) {
    val pagerState = rememberPagerState(pageCount = {
        qrCodeDataList.size
    })
    VerticalPager(
        state = pagerState,
        contentPadding = scaffoldPadding,
        modifier = Modifier
            .fillMaxSize()
    ) { page ->

        val pageData = qrCodeDataList[page]
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(pageData.name)
            Text(
                text = pageData.getUrl()
            )
            val bitmap = pageData.qrCode?.asImageBitmap()
            if (bitmap != null) {
                Image(bitmap = bitmap, contentDescription = pageData.description)
            } else {
                Box(Modifier
                    .size(1000.dp)
                    .background(Color.LightGray))
            }
        }
    }
}

@Composable
fun CustomLoadingIndicator(
    scaffoldPadding: PaddingValues
) {
    Column(
        modifier = Modifier
            .padding(scaffoldPadding)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        CircularProgressIndicator(
            modifier = Modifier
                .width(100.dp)
        )
    }
}

@Composable
fun PromptScreen(
    vCardDataState: VCardData,
    onUpdateVCard: (VCardData) -> Unit,
    scaffoldPadding: PaddingValues,
    onSave: (UrlData) -> Unit,
    onCancel: (Boolean) -> Unit
) {
    var formType by rememberSaveable { mutableStateOf(Form.NO_FORM) }
    var name by rememberSaveable { mutableStateOf("") }
    var url by rememberSaveable { mutableStateOf("") }
    var description by rememberSaveable { mutableStateOf("") }

    val scaleFactor = 1.5f

    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(scaffoldPadding)
            .verticalScroll(rememberScrollState()),
    ) {

        when(formType) {
            Form.NO_FORM -> {
                Column (
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.wrapContentSize()
                ) {
                    ElevatedButton(
                        onClick = { formType = Form.V_CARD_FORM },
                        modifier = Modifier
                            .graphicsLayer(
                                scaleX = scaleFactor,
                                scaleY = scaleFactor,
                                transformOrigin = TransformOrigin.Center
                            )
                    ) {
                        Text("QR Code for V-Card")
                    }
                    Spacer(modifier = Modifier.height(60.dp))
                    ElevatedButton(
                        onClick = { formType = Form.URL_LINK_FORM},
                        modifier = Modifier
                            .graphicsLayer(
                                scaleX = scaleFactor,
                                scaleY = scaleFactor,
                                transformOrigin = TransformOrigin.Center
                            )
                    ) {
                        Text ("QR Code for WebLink")
                    }
                    Spacer(modifier = Modifier.height(60.dp))
                    ElevatedButton(
                        onClick = { onCancel(false) },
                        modifier = Modifier
                            .graphicsLayer(
                                scaleX = scaleFactor,
                                scaleY = scaleFactor,
                                transformOrigin = TransformOrigin.Center
                            )
                    ) {
                        Text("Cancel")
                    }
                }
            }
            Form.V_CARD_FORM -> {
                OutlinedTextField(
                    value = vCardDataState.fullName,
                    onValueChange = { onUpdateVCard(vCardDataState.copy(fullName = it)) },
                    label = { Text("Full Name") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp, end = 8.dp)
                )

                OutlinedTextField(
                    value = vCardDataState.phone ?: "",
                    onValueChange = { onUpdateVCard(vCardDataState.copy(phone = it)) },
                    label = { Text("Phone") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp, end = 8.dp)
                )

                OutlinedTextField(
                    value = vCardDataState.email ?: "",
                    onValueChange = { onUpdateVCard(vCardDataState.copy(email = it)) },
                    label = { Text("Email") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp, end = 8.dp)
                )

                OutlinedTextField(
                    value = vCardDataState.company ?: "",
                    onValueChange = { onUpdateVCard(vCardDataState.copy(company = it)) },
                    label = { Text("Company") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp, end = 8.dp)
                )

                OutlinedTextField(
                    value = vCardDataState.jobTitle ?: "",
                    onValueChange = { onUpdateVCard(vCardDataState.copy(jobTitle = it)) },
                    label = { Text("Job Title") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp, end = 8.dp)
                )

                OutlinedTextField(
                    value = vCardDataState.url ?: "",
                    onValueChange = { onUpdateVCard(vCardDataState.copy(url = it)) },
                    label = { Text("Website/URL") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp, end = 8.dp)
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Notes/Description") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp, end = 8.dp)
                )

                Button(
                    onClick = {
                        onSave(
                            UrlData(
                                name = vCardDataState.fullName,
                                url = vCardDataState.toVCardString(),
                                description = description
                            )
                        )
                    }
                ) {
                    Text("Save")
                }

                Button(
                    onClick = { onCancel(false) }
                ) {
                    Text("Cancel")
                }

            }
            Form.URL_LINK_FORM -> {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Link Name (e.g. My Portfolio)") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp, end = 8.dp)
                )

                OutlinedTextField(
                    value = url,
                    onValueChange = { url = it },
                    label = { Text("URL") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp, end = 8.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri)
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 8.dp, end = 8.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .wrapContentSize()
                        .fillMaxWidth()
                        .padding(start = 8.dp, end = 8.dp)

                ) {
                    Button(
                        onClick = {
                            onSave(
                                UrlData(
                                    name = name,
                                    url = url,
                                    description = description
                                )
                            )
                        }
                    ) {
                        Text("Save")
                    }

                    Button(
                        onClick = { onCancel(false) }
                    ) {
                        Text("Cancel")
                    }
                }
            }
        }
    }
}

@Composable
fun ErrorScreen(
    scaffoldPadding: PaddingValues,
    error: Throwable
) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(scaffoldPadding)
    ) {
        Card {
            Text("Error: ${error.message}")
        }
    }
}

@Composable
fun AddQrCodeFloatingAction(
    onClick: () -> Unit
) {
    ExtendedFloatingActionButton(
        onClick = onClick,
        icon = { Icon(Icons.Filled.Add, "Add Icon") },
        text = { Text("Add QR Code") }
    )
}