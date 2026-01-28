package com.agrocareai.mobile

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.agrocareai.mobile.databinding.ActivityMainBinding
import com.agrocareai.mobile.domain.analyzer.DiseaseAnalyzer
import com.agrocareai.mobile.ui.result.ResultActivity
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.Executors

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val cameraExecutor = Executors.newSingleThreadExecutor()
    private var lastDisease = ""
    private var lastConfidence = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            binding.viewFinder.post { startCamera() }
        } else {
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { if(it) binding.viewFinder.post { startCamera() } else finish() }.launch(Manifest.permission.CAMERA)
        }

        binding.btnCapture.setOnClickListener {
            if (lastDisease.isNotEmpty()) {
                val intent = Intent(this, ResultActivity::class.java).apply {
                    putExtra("DISEASE_NAME", lastDisease)
                    putExtra("CONFIDENCE", lastConfidence)
                }
                startActivity(intent)
            } else {
                Toast.makeText(this, "Scanning...", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun startCamera() {
        val providerFuture = ProcessCameraProvider.getInstance(this)
        providerFuture.addListener({
            try {
                val provider = providerFuture.get()
                val preview = Preview.Builder().build().also { it.setSurfaceProvider(binding.viewFinder.surfaceProvider) }
                val analyzer = ImageAnalysis.Builder()
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .build()
                    .also {
                        it.setAnalyzer(cameraExecutor, DiseaseAnalyzer(this) { d, c ->
                            runOnUiThread {
                                lastDisease = d
                                lastConfidence = c
                                try {
                                    binding.txtOverlayResult.text = "$d ${(c*100).toInt()}%"
                                    binding.progressRisk.progress = (c*100).toInt()
                                } catch (e: Exception) {}
                            }
                        })
                    }
                provider.unbindAll()
                provider.bindToLifecycle(this, CameraSelector.DEFAULT_BACK_CAMERA, preview, analyzer)
            } catch (e: Exception) {
                Toast.makeText(this, "Camera Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }, ContextCompat.getMainExecutor(this))
    }

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}