package com.dev.qros

import android.graphics.Bitmap
import androidx.compose.runtime.mutableStateOf
import androidx.core.graphics.createBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dev.qros.model.QrCodeData
import com.dev.qros.model.QrosUiState
import com.dev.qros.model.UrlData
import com.dev.qros.model.VCardData
import com.dev.qros.model.mapToQrCodeData
import com.dev.qros.roomdb.UrlDataDao
import com.google.zxing.BarcodeFormat
import com.google.zxing.Writer
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QrosViewModel @Inject constructor(
    val urlDataDao: UrlDataDao,
    val qrCodeWriter: Writer
) : ViewModel() {

    init {
        viewModelScope.launch {
            // Just for testing the pager!
            val testSites = listOf("https://google.com", "https://github.com", "https://linkedin.com")
            testSites.forEach { site ->
                urlDataDao.saveUrlData(UrlData(name = "Test $site", url = site, description = "Demo"))
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
    val vCardDataState = _vCardDataState


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
}