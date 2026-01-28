package com.agrocareai.mobile

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.agrocareai.mobile.utils.DiseaseInfo
import com.agrocareai.mobile.utils.DiseaseRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    application: Application
) : AndroidViewModel(application) {

    // DIRECT connection to the local repository
    private val repository = DiseaseRepository(application)

    fun getDiseaseInfo(name: String): DiseaseInfo? {
        return repository.getDiseaseDetails(name)
    }
}