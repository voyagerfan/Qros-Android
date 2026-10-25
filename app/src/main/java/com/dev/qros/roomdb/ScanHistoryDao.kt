package com.dev.qros.roomdb

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dev.qros.model.Scan
import com.dev.qros.model.UrlData
import kotlinx.coroutines.flow.Flow

@Dao
interface ScanHistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertScan(scan: Scan)

    @Query("SELECT * from scans")
    fun getAllScans(): Flow<List<Scan>>
}