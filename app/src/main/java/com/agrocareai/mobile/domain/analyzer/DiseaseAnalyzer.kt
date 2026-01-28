package com.agrocareai.mobile.domain.analyzer

import android.content.Context
import android.graphics.Bitmap
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.agrocareai.mobile.ml.DiseaseDetector
import com.agrocareai.mobile.utils.ImageUtils
import java.util.concurrent.Executors

class DiseaseAnalyzer(
    context: Context,
    private val onResult: (String, Float) -> Unit
) : ImageAnalysis.Analyzer {

    private val detector = DiseaseDetector(context)
    private val executor = Executors.newSingleThreadExecutor()

    // Camera Live Stream
    override fun analyze(image: ImageProxy) {
        val bitmap = ImageUtils.imageProxyToBitmap(image)
        if (bitmap != null) {
            analyzeBitmap(bitmap)
        }
        image.close()
    }

    // Static Image Analysis
    fun analyzeBitmap(bitmap: Bitmap) {
        executor.execute {
            // detector.detect() returns List<Category>
            val results = detector.detect(bitmap)

            // Fixes "maxByOrNull" and "label/score" errors
            val best = results.maxByOrNull { it.score }

            if (best != null) {
                onResult(best.label, best.score)
            } else {
                onResult("Unknown", 0f)
            }
        }
    }
}