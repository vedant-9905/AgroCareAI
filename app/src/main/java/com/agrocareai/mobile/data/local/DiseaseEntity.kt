package com.agrocareai.mobile.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "disease_history")
data class DiseaseEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val diseaseName: String,
    val confidence: Float,
    val imageUrl: String, // We will save the image path
    val timestamp: Long = System.currentTimeMillis()
)