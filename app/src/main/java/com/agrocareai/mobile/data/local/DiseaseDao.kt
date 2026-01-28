package com.agrocareai.mobile.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface DiseaseDao {
    @Insert
    suspend fun insert(record: DiseaseEntity)

    // Flow returns live updates (Reactive)
    @Query("SELECT * FROM disease_history ORDER BY timestamp DESC")
    fun getAllHistory(): Flow<List<DiseaseEntity>>
}