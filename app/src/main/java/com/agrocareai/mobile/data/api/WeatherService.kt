package com.agrocareai.mobile.data.api

import retrofit2.http.GET
import retrofit2.http.Query

// Define the API structure
interface WeatherService {
    @GET("data/2.5/weather")
    suspend fun getCurrentWeather(
        @Query("lat") lat: Double,
        @Query("lon") lon: Double,
        @Query("appid") apiKey: String,
        @Query("units") units: String = "metric"
    ): WeatherResponse
}

// Data Classes to parse the JSON result
data class WeatherResponse(
    val main: MainStats,
    val weather: List<WeatherDescription>,
    val name: String
)

data class MainStats(
    val temp: Float,
    val humidity: Int
)

data class WeatherDescription(
    val main: String,
    val description: String
)