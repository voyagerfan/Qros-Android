package com.dev.qros.roomdb

import androidx.room.Database
import androidx.room.RoomDatabase
import com.dev.qros.model.Scan
import com.dev.qros.model.UrlData

@Database(entities = [UrlData::class, Scan::class], version = 2, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {
    abstract fun urlDataDao(): UrlDataDao
    abstract fun scanHistoryDao(): ScanHistoryDao
}