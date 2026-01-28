package com.agrocareai.mobile.ui.result

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.agrocareai.mobile.MainViewModel
import com.agrocareai.mobile.databinding.ActivityResultBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val name = intent.getStringExtra("DISEASE_NAME") ?: "Unknown"
        val conf = intent.getFloatExtra("CONFIDENCE", 0f)

        binding.txtDiseaseName.text = name
        binding.txtConfidence.text = "${(conf * 100).toInt()}% Confidence"
        binding.progressSeverity.progress = (conf * 100).toInt()

        // Fetch data
        val info = viewModel.getDiseaseInfo(name)

        if (info != null) {
            binding.txtDescription.text = """
                SYMPTOMS: ${info.symptoms}
                
                ORGANIC CURE: ${info.organic_cure}
                
                CHEMICAL CURE: ${info.chemical_cure}
            """.trimIndent()
        } else {
            binding.txtDescription.text = "No details found."
        }

        binding.btnHome.setOnClickListener { finish() }
    }
}