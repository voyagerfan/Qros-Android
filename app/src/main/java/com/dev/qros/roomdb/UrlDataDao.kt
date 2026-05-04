package com.dev.qros.roomdb

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dev.qros.model.UrlData
import kotlinx.coroutines.flow.Flow

@Dao
interface UrlDataDao {
    @Query("SELECT * from urldata")
    fun getAllUrlData(): Flow<List<UrlData>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveUrlData(vararg urlData: UrlData)

    @Delete
    suspend fun deleteUrlData(urlData: UrlData)
}