package com.dev.qros.di

import com.google.zxing.Writer
import com.google.zxing.qrcode.QRCodeWriter
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object QrCodeGenModule {

    @Provides
    @Singleton
    fun providesBarcodeEncoder(): Writer = QRCodeWriter()
}