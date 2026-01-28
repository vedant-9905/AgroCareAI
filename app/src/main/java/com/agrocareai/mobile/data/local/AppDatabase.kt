package com.agrocareai.mobile.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [DiseaseEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun diseaseDao(): DiseaseDao
}