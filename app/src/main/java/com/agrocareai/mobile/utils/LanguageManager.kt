package com.agrocareai.mobile.utils

import android.content.Context
import android.speech.tts.TextToSpeech
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import java.util.Locale

class LanguageManager(context: Context) {

    private var tts: TextToSpeech? = null
    private var isTTSReady = false

    init {
        // Initialize Text To Speech
        tts = TextToSpeech(context) { status ->
            if (status == TextToSpeech.SUCCESS) {
                val result = tts?.setLanguage(Locale("en", "IN")) // Default Indian English
                isTTSReady = result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED
            }
        }
    }

    fun speak(text: String) {
        if (isTTSReady) {
            tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, null)
        }
    }

    fun translateToHindi(text: String, onResult: (String) -> Unit) {
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(TranslateLanguage.HINDI)
            .build()

        val hindiTranslator = Translation.getClient(options)

        // Ensure model is downloaded
        val conditions = DownloadConditions.Builder().requireWifi().build()
        hindiTranslator.downloadModelIfNeeded(conditions)
            .addOnSuccessListener {
                // Model downloaded, now translate
                hindiTranslator.translate(text)
                    .addOnSuccessListener { translatedText -> onResult(translatedText) }
                    .addOnFailureListener { onResult(text) } // Return original on error
            }
            .addOnFailureListener {
                onResult(text) // Return original if model fails
            }
    }

    fun shutdown() {
        tts?.stop()
        tts?.shutdown()
    }
}