package com.agrocareai.mobile.data.model

import com.google.gson.annotations.SerializedName

// This matches the OpenWeatherMap JSON structure
data class WeatherResponse(
    @SerializedName("weather") val weather: List<WeatherDescription>,
    @SerializedName("main") val main: MainStats
)

data class WeatherDescription(
    @SerializedName("main") val condition: String, // e.g., "Rain"
    @SerializedName("description") val description: String
)

data class MainStats(
    @SerializedName("humidity") val humidity: Int,
    @SerializedName("temp") val temp: Float
)