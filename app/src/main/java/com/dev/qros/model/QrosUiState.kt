package com.dev.qros.model

sealed interface QrosUiState<out T> {
    data class Success<T>(val data: T) : QrosUiState<T>
    data class Error(val error: Throwable): QrosUiState<Nothing>
    object Loading : QrosUiState<Nothing>
    object Empty: QrosUiState<Nothing>
}