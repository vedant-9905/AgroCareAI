package com.agrocareai.mobile.ml

import android.content.Context
import android.graphics.Bitmap
import com.agrocareai.mobile.utils.Constants
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.task.vision.classifier.ImageClassifier

class DiseaseDetector(context: Context) {
    private val classifier: ImageClassifier? = try {
        ImageClassifier.createFromFile(context, Constants.MODEL_PATH)
    } catch (e: Exception) {
        null
    }

    // Must return List<Category>
    fun detect(bitmap: Bitmap): List<org.tensorflow.lite.support.label.Category> {
        if (classifier == null) return emptyList()
        val tensorImage = TensorImage.fromBitmap(bitmap)
        val results = classifier.classify(tensorImage)
        return results.flatMap { it.categories }
    }
}