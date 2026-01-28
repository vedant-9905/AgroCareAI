package com.agrocareai.mobile.ui.history

import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.agrocareai.mobile.MainViewModel
import com.agrocareai.mobile.databinding.ActivityHistoryBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HistoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistoryBinding
    // We reuse MainViewModel because it already holds the repository logic
    private val viewModel: MainViewModel by viewModels()
    private val adapter = HistoryAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Inflate the layout using Binding
        binding = ActivityHistoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupUI()
        observeData()
    }

    private fun setupUI() {
        binding.rvHistory.layoutManager = LinearLayoutManager(this)
        binding.rvHistory.adapter = adapter

        binding.toolbar.setNavigationOnClickListener {
            finish() // Close screen when back arrow clicked
        }
    }

    private fun observeData() {
        lifecycleScope.launch {
            viewModel.historyFlow.collect { list ->
                if (list.isEmpty()) {
                    binding.txtEmpty.visibility = View.VISIBLE
                    binding.rvHistory.visibility = View.GONE
                } else {
                    binding.txtEmpty.visibility = View.GONE
                    binding.rvHistory.visibility = View.VISIBLE
                    adapter.submitList(list)
                }
            }
        }
    }
}