package com.dev.qros

import android.graphics.Bitmap
import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageProxy
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.util.copy
import com.dev.qros.extensions.toContactInfo
import com.dev.qros.extensions.toScan
import com.dev.qros.model.CameraScreenState
import com.dev.qros.model.ContactInfo
import com.dev.qros.model.QrCodeData
import com.dev.qros.model.QrosBarcode
import com.dev.qros.model.QrosUiState
import com.dev.qros.model.Scan
import com.dev.qros.model.ScanState
import com.dev.qros.model.UrlData
import com.dev.qros.model.VCardData
import com.dev.qros.model.mapToQrCodeData
import com.dev.qros.roomdb.ScanHistoryDao
import com.dev.qros.roomdb.UrlDataDao
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import com.google.zxing.BarcodeFormat
import com.google.zxing.Writer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class QrosViewModel @Inject constructor(
    val urlDataDao: UrlDataDao,
    val scanHistoryDao: ScanHistoryDao,
    val qrCodeWriter: Writer,
    val barcodeScanner: BarcodeScanner,
) : ViewModel() {


    init {
        viewModelScope.launch {
            // Just for testing the pager!
            val testSites =
                listOf("https://google.com", "https://github.com", "https://linkedin.com")
            testSites.forEach { site ->
                urlDataDao.saveUrlData(
                    UrlData(
                        name = "Test $site",
                        url = site,
                        description = "Demo"
                    )
                )
            }
        }
    }

    /* ----------------- State Flow Variables ------------------ */
    val qrosUiState: StateFlow<QrosUiState<List<QrCodeData>>> = urlDataDao
        .getAllUrlData()
        .map { list ->
            if (list.isEmpty()) QrosUiState.Empty
            else {
                val qrDataList = list.map {
                    it.mapToQrCodeData().copy(qrCode = createQrCode(qrCodeWriter, it.url))
                }
                QrosUiState.Success(data = qrDataList)
            }
        }
        .flowOn(Dispatchers.Default)
        .catch { e -> emit(QrosUiState.Error(e)) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = QrosUiState.Loading
        )

    private val _vCardDataState = MutableStateFlow(VCardData(fullName = ""))
    val vCardDataState = _vCardDataState.asStateFlow()

    private val _cameraScreenState = MutableStateFlow(
        CameraScreenState(
            isScanningEnabled = true,
            shouldShowBottomDialogCta = false,
            currentQrosBarcodeList = emptyList(),
            scanState = ScanState.Scanning
        )
    )
    val cameraScreenState = _cameraScreenState.asStateFlow()


    /* ----------------- ViewModel Functions -------------------- */

    fun updateVCardData(vCardData: VCardData) {
        _vCardDataState.value = vCardData
    }

    private fun createQrCode(qrCodeWriter: Writer, url: String): Bitmap {
        val size = 1000
        val bitMatrix = qrCodeWriter.encode(
            url,
            BarcodeFormat.QR_CODE,
            size,
            size
        )

        val pixels = IntArray(size * size)
        for (y in 0 until size) {
            val offset = y * size
            for (x in 0 until size) {
                pixels[offset + x] = if (bitMatrix.get(x, y)) -0x1000000 else -0x1
            }
        }
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        bitmap.setPixels(pixels, 0, size, 0, 0, size, size)
        return bitmap
    }

    suspend fun addNewUrl(urlData: UrlData) {
        urlDataDao.saveUrlData(urlData)
    }

    @OptIn(ExperimentalGetImage::class)
    fun processImage(imageProxy: ImageProxy) {
        when (_cameraScreenState.value.scanState) {
            is ScanState.Scanning -> {
                Log.d("scanState", "scanState is scanning")
            }

            is ScanState.Ready -> {
                Log.d("scanState", "scanState is ready")
            }

            is ScanState.Error -> {
                Log.d("scanState", "scanState is error")
            }

            is ScanState.Success -> {
                Log.d("scanState", "scanState is success")
            }
        }


        if (!_cameraScreenState.value.isScanningEnabled) {
            Log.d("QrosScanner", "isScanningEnabled is false, returning")
            imageProxy.close()
            return
        }
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            val qrosBarcodeList = mutableListOf<QrosBarcode>()

            barcodeScanner.process(image)
                .addOnSuccessListener { barcodes ->
                    Log.d("onSuccess", "Scan success callback hit! Found ${barcodes.size} barcodes.")
                    if (barcodes.isNotEmpty()) {
                        Log.d("barcodesFound!", "Scan success callback hit! Found ${barcodes.size} barcodes.")
                    }
                    barcodes.forEachIndexed { index, code ->
                        // convert barcode to scan object and insert into database
                        viewModelScope.launch { scanHistoryDao.insertScan(code.toScan()) }

                        // parse barcode into custom objects, add to captured barcodeList
                        when (code.valueType) {
                            Barcode.TYPE_CONTACT_INFO -> {
                                qrosBarcodeList.add(
                                    QrosBarcode.Contact(
                                        contactInfo = code.toContactInfo(),
                                        key = index
                                    )
                                )
                            }

                            Barcode.TYPE_URL -> {
                                qrosBarcodeList.add(
                                    QrosBarcode.Url(
                                        url = code?.url?.url ?: "",
                                        key = index
                                    )
                                )
                            }
                            // add more types as needed
                        }
                    }
                    if(qrosBarcodeList.isNotEmpty()) {
                        _cameraScreenState.value = _cameraScreenState.value.copy(
                            isScanningEnabled = false,
                            scanState = ScanState.Success(data = qrosBarcodeList),
                            currentQrosBarcodeList = qrosBarcodeList
                        )
                        imageProxy.close()
                    }

                }
                .addOnFailureListener { e ->
                    updateScanStateToError(e)
                    Log.e("CameraViewModel", "Barcode scanning failed", e)
                }
                .addOnCompleteListener {
                    imageProxy.close()
                    // TODO: keep listener for now
                }
        } else {
            // If mediaImage was null, close it immediately
            imageProxy.close()
        }
    }

    private fun updateCurrentQrosBarcodeList(qrosBarcodeList: List<QrosBarcode>) {
        _cameraScreenState.value = _cameraScreenState.value.copy(
            currentQrosBarcodeList = qrosBarcodeList
        )
    }

    fun updateActionBarcodeListItem(itemKey: Int) {
        val updatedBarcodeList = cameraScreenState.value.currentQrosBarcodeList
            .map { barcode ->
                if (barcode.key == itemKey) {
                    val barcodeUpdate = when (barcode) {
                        is QrosBarcode.Contact -> barcode.copy(isActioned = true)
                        is QrosBarcode.Url -> barcode.copy(isActioned = true)
                    }
                    barcodeUpdate
                } else {
                    barcode
                }
            }
        updateCurrentQrosBarcodeList(updatedBarcodeList)
    }

    fun shouldShowBottomDialogCta(shouldShow: Boolean) {
        _cameraScreenState.value = _cameraScreenState.value.copy(
            shouldShowBottomDialogCta = shouldShow
        )
    }

    fun stopScanning() {
        _cameraScreenState.value = _cameraScreenState.value.copy(
            isScanningEnabled = false,
            scanState = ScanState.Ready
        )
    }

    fun startScanning() {
        _cameraScreenState.value = _cameraScreenState.value.copy(
            isScanningEnabled = true,
            scanState = ScanState.Scanning
        )
    }

    fun updateScanStateToError(e: Throwable) {
        _cameraScreenState.value = _cameraScreenState.value.copy(
            isScanningEnabled = true,
            scanState = ScanState.Error(error = e)
        )
    }
}
