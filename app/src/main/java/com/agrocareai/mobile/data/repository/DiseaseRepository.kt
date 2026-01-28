package com.agrocareai.mobile.data.repository

import com.agrocareai.mobile.data.api.WeatherService
import com.agrocareai.mobile.data.local.DiseaseDao
import com.agrocareai.mobile.data.local.DiseaseEntity
import com.agrocareai.mobile.utils.Constants
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DiseaseRepository @Inject constructor(
    private val diseaseDao: DiseaseDao,
    private val weatherService: WeatherService
) {

    // 1. Save a new scan to offline history
    suspend fun saveRecord(record: DiseaseEntity) {
        diseaseDao.insert(record)
    }

    // 2. Get all past scans (Reactive Flow)
    fun getHistory(): Flow<List<DiseaseEntity>> {
        return diseaseDao.getAllHistory()
    }

    // 3. Get Live Weather (Returns null if offline/error)
    suspend fun getLocalWeather(lat: Double, lon: Double, apiKey: String): String? {
        return try {
            val response = weatherService.getCurrentWeather(lat, lon, apiKey)
            if (response.isSuccessful) {
                val weather = response.body()
                // Format: "Rain, 85% Humidity"
                "${weather?.weather?.firstOrNull()?.condition}, ${weather?.main?.humidity}% Hum."
            } else {
                null
            }
        } catch (e: Exception) {
            null // Fallback for offline mode
        }
    }
}