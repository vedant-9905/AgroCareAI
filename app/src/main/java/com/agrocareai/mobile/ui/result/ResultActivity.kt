package com.agrocareai.mobile.ui.result

import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.agrocareai.mobile.MainViewModel
import com.agrocareai.mobile.databinding.ActivityResultBinding
import com.agrocareai.mobile.domain.analyzer.DiseaseAnalyzer
import dagger.hilt.android.AndroidEntryPoint
import java.io.File

@AndroidEntryPoint
class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding
    // We reuse MainViewModel to access LanguageManager
    private val viewModel: MainViewModel by viewModels()
    private lateinit var analyzer: DiseaseAnalyzer

    // Store original text to toggle back and forth
    private var originalDescription = ""
    private var isTranslated = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupAnalyzer()
        setupSmartFeatures()
        processIntentData()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupSmartFeatures() {
        // 1. Text-to-Speech (Speaker Button)
        binding.btnSpeak.setOnClickListener {
            val textToSpeak = binding.txtDescription.text.toString()
            if (textToSpeak.isNotEmpty()) {
                viewModel.languageManager.speak(textToSpeak)
                Toast.makeText(this, "Speaking...", Toast.LENGTH_SHORT).show()
            }
        }

        // 2. Translation (Language Button)
        binding.btnTranslate.setOnClickListener {
            if (isTranslated) {
                // Revert to English
                binding.txtDescription.text = originalDescription
                isTranslated = false
                Toast.makeText(this, "English", Toast.LENGTH_SHORT).show()
            } else {
                // Translate to Hindi
                val text = binding.txtDescription.text.toString()
                if (text.isNotEmpty()) {
                    binding.btnTranslate.isEnabled = false // Prevent double tap
                    Toast.makeText(this, "Translating...", Toast.LENGTH_SHORT).show()

                    viewModel.languageManager.translateToHindi(text) { translated ->
                        runOnUiThread {
                            binding.txtDescription.text = translated
                            binding.btnTranslate.isEnabled = true
                            isTranslated = true
                        }
                    }
                }
            }
        }

        // 3. Save Button (Placeholder for PDF)
        binding.btnSave.setOnClickListener {
            Toast.makeText(this, "PDF Report Saved to History", Toast.LENGTH_SHORT).show()
            // TODO: Trigger PDF Generation Logic here later
        }
    }

    private fun setupAnalyzer() {
        analyzer = DiseaseAnalyzer(this) { disease, confidence ->
            runOnUiThread {
                updateUI(disease, confidence)
            }
        }
    }

    private fun processIntentData() {
        val imageUriString = intent.getStringExtra("IMAGE_URI")
        if (imageUriString != null) {
            val uri = Uri.parse(imageUriString)
            binding.imgAnalyzed.setImageURI(uri)
            try {
                val inputStream = contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                analyzer.analyzeBitmap(bitmap)
            } catch (e: Exception) {
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show()
            }
            return
        }

        val imagePath = intent.getStringExtra("IMAGE_PATH")
        if (imagePath != null) {
            val file = File(imagePath)
            if (file.exists()) {
                val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                binding.imgAnalyzed.setImageBitmap(bitmap)
                analyzer.analyzeBitmap(bitmap)
            }
        }
    }

    private fun updateUI(disease: String, confidence: Float) {
        binding.txtDiseaseName.text = disease
        val percentage = (confidence * 100).toInt()
        binding.txtConfidence.text = "$percentage% Confidence"
        binding.progressSeverity.progress = percentage

        // Populate Description
        if (disease.contains("Healthy", ignoreCase = true)) {
            originalDescription = "Your crop looks healthy! Keep monitoring regularly for any changes. Ensure proper watering and soil nutrition."
            binding.progressSeverity.progressTintList = android.content.res.ColorStateList.valueOf(getColor(android.R.color.holo_green_dark))
        } else {
            originalDescription = "Early symptoms of $disease detected. This is commonly caused by fungal or bacterial infection. Immediate treatment is recommended to prevent spread."
            binding.progressSeverity.progressTintList = android.content.res.ColorStateList.valueOf(getColor(android.R.color.holo_red_dark))
        }

        // Set text
        binding.txtDescription.text = originalDescription
    }
}