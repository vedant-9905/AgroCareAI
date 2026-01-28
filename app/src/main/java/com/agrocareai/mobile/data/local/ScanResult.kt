package com.agrocareai.mobile.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "scan_results")
data class ScanResult(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val diseaseName: String, // Adapter uses this
    val confidence: Float,   // Adapter uses this
    val timestamp: Long,     // Adapter uses this
    val imagePath: String    // Adapter uses this (even if empty)
)