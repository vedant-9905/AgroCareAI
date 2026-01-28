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
    private val viewModel: MainViewModel by viewModels() // Reuse for saving history later
    private lateinit var analyzer: DiseaseAnalyzer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupAnalyzer()
        processIntentData()
    }

    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "" // Hide default title
        binding.toolbar.setNavigationOnClickListener { finish() }
    }

    private fun setupAnalyzer() {
        // Initialize Analyzer with a callback to update UI
        analyzer = DiseaseAnalyzer(this) { disease, confidence ->
            runOnUiThread {
                updateUI(disease, confidence)
            }
        }
    }

    private fun processIntentData() {
        // 1. Handle Gallery Selection (URI)
        val imageUriString = intent.getStringExtra("IMAGE_URI")
        if (imageUriString != null) {
            val uri = Uri.parse(imageUriString)
            binding.imgAnalyzed.setImageURI(uri) // Show Image

            // Convert URI to Bitmap for Analysis
            try {
                val inputStream = contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                analyzer.analyzeBitmap(bitmap) // Run AI
            } catch (e: Exception) {
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show()
            }
            return
        }

        // 2. Handle Camera Capture (File Path)
        val imagePath = intent.getStringExtra("IMAGE_PATH")
        if (imagePath != null) {
            val file = File(imagePath)
            if (file.exists()) {
                val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                binding.imgAnalyzed.setImageBitmap(bitmap) // Show Image

                // If detection was passed from Camera Live View, use it.
                // Otherwise, re-analyze (better for high-res).
                val passedDisease = intent.getStringExtra("DISEASE_NAME")
                val passedScore = intent.getFloatExtra("CONFIDENCE", 0f)

                if (passedDisease != null && passedDisease != "Scanning...") {
                    updateUI(passedDisease, passedScore)
                } else {
                    analyzer.analyzeBitmap(bitmap) // Re-analyze
                }
            }
        }
    }

    private fun updateUI(disease: String, confidence: Float) {
        binding.txtDiseaseName.text = disease
        val percentage = (confidence * 100).toInt()
        binding.txtConfidence.text = "$percentage%"

        // Update Risk Bar based on confidence
        binding.progressSeverity.progress = percentage

        // Dummy Description Logic (We will replace this with Encyclopedia lookup later)
        if (disease.contains("Healthy", ignoreCase = true)) {
            binding.txtDescription.text = "Your crop looks healthy! Keep monitoring regularly."
            binding.progressSeverity.progressTintList = android.content.res.ColorStateList.valueOf(getColor(android.R.color.holo_green_dark))
        } else {
            binding.txtDescription.text = "Early symptoms detected. $disease is a common fungal infection. Check the Treatment tab for organic and chemical remedies."
            binding.progressSeverity.progressTintList = android.content.res.ColorStateList.valueOf(getColor(android.R.color.holo_red_dark))
        }
    }
}