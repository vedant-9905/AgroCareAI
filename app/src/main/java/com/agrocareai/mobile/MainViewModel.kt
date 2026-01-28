package com.agrocareai.mobile

import android.annotation.SuppressLint
import android.app.Application
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.agrocareai.mobile.data.api.WeatherService
import com.agrocareai.mobile.utils.Constants
import com.agrocareai.mobile.utils.DiseaseInfo
import com.agrocareai.mobile.utils.DiseaseRepository
import com.agrocareai.mobile.utils.LanguageManager
import com.google.android.gms.location.LocationServices
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AppState(
    val weatherInfo: String = "Loading Weather...",
    val isRaining: Boolean = false
)

@HiltViewModel
class MainViewModel @Inject constructor(
    application: Application,
    private val weatherService: WeatherService
) : AndroidViewModel(application) {

    private val _state = MutableStateFlow(AppState())
    val state = _state.asStateFlow()

    val languageManager = LanguageManager(application)
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(application)

    // --- NEW: Encyclopedia Repository ---
    private val repository = DiseaseRepository(application)

    init {
        fetchLocationAndWeather()
    }

    // Helper to get cures
    fun getDiseaseDetails(name: String): DiseaseInfo? {
        return repository.getDiseaseByName(name)
    }

    @SuppressLint("MissingPermission")
    fun fetchLocationAndWeather() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
            if (location != null) {
                fetchWeather(location.latitude, location.longitude)
            } else {
                _state.value = _state.value.copy(weatherInfo = "Location unavailable")
            }
        }
    }

    private fun fetchWeather(lat: Double, lon: Double) {
        viewModelScope.launch {
            try {
                val response = weatherService.getCurrentWeather(lat, lon, Constants.WEATHER_API_KEY)
                val info = "${response.name}: ${response.main.temp}°C, ${response.weather[0].description}"

                _state.value = _state.value.copy(
                    weatherInfo = info,
                    isRaining = response.weather[0].main.contains("Rain", ignoreCase = true)
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(weatherInfo = "Weather Error: Check Internet")
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        languageManager.shutdown()
    }
}