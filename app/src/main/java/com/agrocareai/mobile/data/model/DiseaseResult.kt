package com.agrocareai.mobile.data.model

data class DiseaseResult(
    val name: String,
    val confidence: Float,
    val diagnosis: String,
    val prevention: String
)
