package com.agrocareai.mobile.di

import android.content.Context
import androidx.room.Room
import com.agrocareai.mobile.data.local.AppDatabase
import com.agrocareai.mobile.data.local.DiseaseDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "agrocare_db"
        ).build()
    }

    @Provides
    fun provideDiseaseDao(database: AppDatabase): DiseaseDao {
        return database.diseaseDao()
    }
}