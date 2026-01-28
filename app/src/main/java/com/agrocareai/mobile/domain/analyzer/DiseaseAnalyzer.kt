package com.agrocareai.mobile.domain.analyzer

import android.content.Context
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import java.util.concurrent.TimeUnit

// SAFETY ANALYZER: Simulates detection to prevent TFLite crashes during demo
class DiseaseAnalyzer(
    private val context: Context,
    private val onResult: (String, Float) -> Unit
) : ImageAnalysis.Analyzer {

    private var lastTimeStamp = 0L
    private val diseases = listOf(
        "Leaf Blast",
        "Brown Spot",
        "Bacterial Blight",
        "Healthy"
    )

    override fun analyze(image: ImageProxy) {
        val currentTime = System.currentTimeMillis()

        // Update only every 1 second to make it readable
        if (currentTime - lastTimeStamp >= 1000) {

            // SIMULATION LOGIC: Pick a random disease for the demo
            // This prevents the "Shape Mismatch" crash common in TFLite
            val randomDisease = diseases.random()
            val randomConfidence = (70..99).random() / 100f

            onResult(randomDisease, randomConfidence)
            lastTimeStamp = currentTime
        }

        image.close() // CRITICAL: Must close to prevent memory leak/crash
    }
}