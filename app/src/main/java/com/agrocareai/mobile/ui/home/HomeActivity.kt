package com.agrocareai.mobile.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import com.agrocareai.mobile.MainActivity
import com.agrocareai.mobile.databinding.ActivityHomeBinding
import com.agrocareai.mobile.ui.history.HistoryActivity
import com.agrocareai.mobile.ui.result.ResultActivity
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityHomeBinding

    // RANDOMIZER: Gives different results for different images
    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            val diseases = listOf("Leaf Blast", "Brown Spot", "Bacterial Blight", "Healthy")
            val randomDisease = diseases.random()
            val randomConfidence = (75..98).random() / 100f

            val intent = Intent(this, ResultActivity::class.java).apply {
                putExtra("DISEASE_NAME", randomDisease)
                putExtra("CONFIDENCE", randomConfidence)
            }
            startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.txtWeather.text = "Mumbai, 28°C\nCloudy"

        binding.btnMenu.setOnClickListener { binding.drawerLayout.openDrawer(GravityCompat.START) }

        binding.cardScan.setOnClickListener { startActivity(Intent(this, MainActivity::class.java)) }
        binding.cardGallery.setOnClickListener { galleryLauncher.launch("image/*") }
        binding.cardHistory.setOnClickListener { startActivity(Intent(this, HistoryActivity::class.java)) }

        binding.navView.setNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }
}