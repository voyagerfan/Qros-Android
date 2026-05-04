package com.dev.qros.di

import android.content.Context
import androidx.room.Room
import com.dev.qros.roomdb.AppDatabase
import com.dev.qros.roomdb.UrlDataDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import kotlin.jvm.java

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun providesRoomDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java, "qros_db"
        ).build()
    }

    @Provides
    fun providesDaoObject(db: AppDatabase): UrlDataDao {
        return db.urlDataDao()
    }
}