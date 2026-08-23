package com.dev.qros.roomdb

import androidx.compose.ui.text.LinkAnnotation
import androidx.room.Database
import androidx.room.RoomDatabase
import com.dev.qros.model.UrlData

@Database(entities = [UrlData::class], version = 1, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {
    abstract fun urlDataDao(): UrlDataDao
    abstract fun scanHistoryDao(): ScanHistoryDao
}