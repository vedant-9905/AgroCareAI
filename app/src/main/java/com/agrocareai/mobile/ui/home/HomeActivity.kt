package com.agrocareai.mobile.ui.home

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.lifecycle.lifecycleScope
import com.agrocareai.mobile.MainActivity
import com.agrocareai.mobile.MainViewModel
import com.agrocareai.mobile.databinding.ActivityHomeBinding
import com.agrocareai.mobile.ui.history.HistoryActivity
import com.agrocareai.mobile.ui.result.ResultActivity
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityHomeBinding
    private val viewModel: MainViewModel by viewModels()

    // Permissions for Smart Weather
    private val locationPermissionRequest = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) ||
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false)) {
            viewModel.fetchLocationAndWeather()
        } else {
            Toast.makeText(this, "Location denied. Weather unavailable.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupProgrammaticMenu() // <--- NEW: Builds menu without XML
        setupClickListeners()
        setupSmartWeather()
    }

    private fun setupProgrammaticMenu() {
        val menu: Menu = binding.navView.menu
        menu.clear() // clear any old XML junk

        // 1. Dashboard Group
        val group1 = menu.addSubMenu("Dashboard")
        group1.add(0, 101, 0, "Home").setIcon(android.R.drawable.ic_menu_compass)
        group1.add(0, 102, 0, "Farmer Profile").setIcon(android.R.drawable.ic_menu_my_calendar)
        group1.add(0, 103, 0, "Scan History").setIcon(android.R.drawable.ic_menu_recent_history)

        // 2. Settings Group
        val group2 = menu.addSubMenu("Settings")
        group2.add(0, 201, 0, "Language").setIcon(android.R.drawable.ic_menu_sort_by_size)
        group2.add(0, 202, 0, "Dark Mode").setIcon(android.R.drawable.ic_menu_day)

        // Set Listener
        binding.navView.setNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            101 -> { /* Already on Home */ }
            102 -> Toast.makeText(this, "Profile Coming Soon", Toast.LENGTH_SHORT).show()
            103 -> startActivity(Intent(this, HistoryActivity::class.java))
            201 -> Toast.makeText(this, "Language Settings", Toast.LENGTH_SHORT).show()
            202 -> Toast.makeText(this, "Dark Mode Toggle", Toast.LENGTH_SHORT).show()
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun setupSmartWeather() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            viewModel.fetchLocationAndWeather()
        } else {
            locationPermissionRequest.launch(arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ))
        }

        lifecycleScope.launch {
            viewModel.state.collect { state ->
                binding.txtWeather.text = state.weatherInfo
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnMenu.setOnClickListener { binding.drawerLayout.openDrawer(GravityCompat.START) }
        binding.cardScan.setOnClickListener { startActivity(Intent(this, MainActivity::class.java)) }
        binding.cardGallery.setOnClickListener { galleryLauncher.launch("image/*") }
        binding.cardHistory.setOnClickListener { startActivity(Intent(this, HistoryActivity::class.java)) }

        // Placeholders
        val placeholder = { Toast.makeText(this, "Coming Soon", Toast.LENGTH_SHORT).show() }

        // These IDs must exist in activity_home.xml
        binding.cardEncyclopedia.setOnClickListener { placeholder() }
        binding.cardBot.setOnClickListener { placeholder() }
        binding.cardProfile.setOnClickListener { placeholder() }
    }

    private val galleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) {
            val intent = Intent(this, ResultActivity::class.java)
            intent.putExtra("IMAGE_URI", uri.toString())
            startActivity(intent)
        }
    }
}