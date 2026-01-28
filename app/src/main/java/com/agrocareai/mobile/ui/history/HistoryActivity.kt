package com.agrocareai.mobile.ui.history

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.agrocareai.mobile.data.local.ScanResult
import com.agrocareai.mobile.databinding.ActivityHistoryBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryBinding
    private val adapter = HistoryAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvHistory.layoutManager = LinearLayoutManager(this)
        binding.rvHistory.adapter = adapter

        // HACKATHON MODE: Dummy Data so the screen looks full
        val dummyData = listOf(
            ScanResult(0, "Leaf Blast", 0.95f, System.currentTimeMillis(), ""),
            ScanResult(1, "Brown Spot", 0.88f, System.currentTimeMillis() - 86400000, ""),
            ScanResult(2, "Healthy", 0.99f, System.currentTimeMillis() - 172800000, "")
        )
        adapter.submitList(dummyData)
    }
}