package com.agrocareai.mobile.ui.home

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.agrocareai.mobile.MainActivity
import com.agrocareai.mobile.R
import com.agrocareai.mobile.databinding.ActivityHomeBinding
import com.agrocareai.mobile.ui.history.HistoryActivity
import com.agrocareai.mobile.ui.result.ResultActivity
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        // 1. Navigation Drawer
        binding.btnMenu.setOnClickListener {
            binding.drawerLayout.openDrawer(GravityCompat.START)
        }

        // 2. Open Live Camera
        binding.cardScan.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        // 3. Open Gallery (NEW FEATURE)
        binding.cardGallery.setOnClickListener {
            galleryLauncher.launch("image/*")
        }

        // 4. Open History
        binding.cardHistory.setOnClickListener {
            val intent = Intent(this, HistoryActivity::class.java)
            startActivity(intent)
        }

        // 5. Encyclopedia (Coming Soon)
        binding.cardEncyclopedia.setOnClickListener {
            Toast.makeText(this, "Encyclopedia Feature Coming Soon", Toast.LENGTH_SHORT).show()
        }
    }

    // Handles the Image Selection from Gallery
    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            // Send URI to the Result Screen
            val intent = Intent(this, ResultActivity::class.java)
            intent.putExtra("IMAGE_URI", uri.toString())
            startActivity(intent)
        }
    }
}