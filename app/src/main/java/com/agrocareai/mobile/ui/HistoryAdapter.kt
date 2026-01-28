package com.agrocareai.mobile.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.agrocareai.mobile.data.local.DiseaseEntity
import com.agrocareai.mobile.databinding.ItemHistoryBinding
import java.text.SimpleDateFormat
import java.util.*

class HistoryAdapter : ListAdapter<DiseaseEntity, HistoryAdapter.ViewHolder>(DiffCallback) {

    class ViewHolder(private val binding: ItemHistoryBinding) : RecyclerView.ViewHolder(binding.root) {
        private val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())

        fun bind(item: DiseaseEntity) {
            binding.historyDiseaseName.text = item.diseaseName
            binding.historyConfidence.text = "Confidence: ${(item.confidence * 100).toInt()}%"
            binding.historyDate.text = dateFormat.format(Date(item.timestamp))
            // Image loading would usually happen here with Glide/Coil
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemHistoryBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object DiffCallback : DiffUtil.ItemCallback<DiseaseEntity>() {
        override fun areItemsTheSame(oldItem: DiseaseEntity, newItem: DiseaseEntity): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: DiseaseEntity, newItem: DiseaseEntity): Boolean {
            return oldItem == newItem
        }
    }
}