package com.agrocareai.mobile.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.agrocareai.mobile.data.local.DiseaseEntity
import com.agrocareai.mobile.data.repository.DiseaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DiseaseViewModel @Inject constructor(
    private val repository: DiseaseRepository
) : ViewModel() {

    val history = repository.getHistory()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun saveScanResult(label: String, confidence: Float, imagePath: String) {
        viewModelScope.launch {
            repository.saveRecord(
                DiseaseEntity(
                    diseaseName = label,
                    confidence = confidence,
                    imageUrl = imagePath
                )
            )
        }
    }

    suspend fun checkWeatherRisk(lat: Double, lon: Double): String? {
        // In a real app, API key would be in BuildConfig or Secrets
        return repository.getLocalWeather(lat, lon, "YOUR_API_KEY")
    }
}