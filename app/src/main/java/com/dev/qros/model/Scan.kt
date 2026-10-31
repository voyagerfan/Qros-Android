package com.dev.qros.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Entity(tableName = "scans")
data class Scan(
    @PrimaryKey(autoGenerate = true) val uid: Int = 0,
    @ColumnInfo val type: ScanType,
    @ColumnInfo val payload: String? = null,
    @ColumnInfo val timestamp: Long
) {
    fun getFormattedTimestamp(): String {
        return Instant.ofEpochMilli(timestamp)
            .atZone(ZoneId.systemDefault())
            .format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy 'at' h:mma", Locale.ENGLISH))
    }
}

