package com.agrocareai.mobile

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import com.agrocareai.mobile.databinding.ActivityMainBinding
import com.agrocareai.mobile.domain.analyzer.DiseaseAnalyzer
import com.agrocareai.mobile.ui.history.HistoryActivity
import com.agrocareai.mobile.ui.result.ResultActivity
import com.agrocareai.mobile.utils.FileUtils
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var cameraExecutor: ExecutorService

    // We store the latest AI result here in real-time
    private var lastDetectedDisease: String = "Analyzing..."
    private var lastConfidence: Float = 0.0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Setup UI
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 2. Setup Camera Thread
        cameraExecutor = Executors.newSingleThreadExecutor()

        // 3. Check Permissions & Start
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }

        // 4. Setup Buttons
        setupInteractions()
    }

    private fun setupInteractions() {
        // Capture Button: Freezes the moment and moves to diagnosis
        binding.btnCapture.setOnClickListener {
            captureAndDiagnose()
        }

        // History Button
        binding.btnHistory.setOnClickListener {
            startActivity(Intent(this, HistoryActivity::class.java))
        }
    }

    private fun captureAndDiagnose() {
        // 1. Get the current frame as a bitmap
        val bitmap = binding.viewFinder.bitmap

        if (bitmap != null) {
            // 2. Save it to internal storage
            val savedPath = FileUtils.saveBitmapToInternalStorage(this, bitmap)

            // 3. Pass data to ResultActivity
            val intent = Intent(this, ResultActivity::class.java).apply {
                putExtra("IMAGE_PATH", savedPath)
                putExtra("DISEASE_NAME", lastDetectedDisease)
                putExtra("CONFIDENCE", lastDetectedConfidence)
            }
            startActivity(intent)

            // Optional: Add a nice slide transition here
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        } else {
            Toast.makeText(this, "Camera initializing...", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            // A. Preview (The ViewFinder)
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
            }

            // B. Analyzer (The Background Brain)
            val imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
                    it.setAnalyzer(cameraExecutor, DiseaseAnalyzer(this) { disease, score ->
                        // Update our local variables (Background Thread)
                        lastDetectedDisease = disease
                        lastDetectedConfidence = score

                        // Update UI Status Text (Main Thread)
                        runOnUiThread {
                            if (score > 0.7) {
                                binding.txtStatus.text = "SUBJECT DETECTED"
                                binding.txtStatus.setTextColor(getColor(R.color.neon_green))
                            } else {
                                binding.txtStatus.text = "SCANNING..."
                                binding.txtStatus.setTextColor(getColor(R.color.text_secondary))
                            }
                        }
                    })
                }

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this, CameraSelector.DEFAULT_BACK_CAMERA, preview, imageAnalyzer
                )
            } catch (exc: Exception) {
                Log.e("AgroCare", "Camera binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) startCamera() else Toast.makeText(this, "Permissions Required", Toast.LENGTH_SHORT).show()
        }

    private fun allPermissionsGranted() = ContextCompat.checkSelfPermission(
        baseContext, Manifest.permission.CAMERA
    ) == PackageManager.PERMISSION_GRANTED

    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }
}