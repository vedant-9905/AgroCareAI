package com.agrocareai.mobile.ui.history

import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.agrocareai.mobile.data.local.DiseaseEntity
import com.agrocareai.mobile.databinding.ItemHistoryBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class HistoryAdapter : ListAdapter<DiseaseEntity, HistoryAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(private val binding: ItemHistoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DiseaseEntity) {
            binding.txtDiseaseTitle.text = item.diseaseName
            binding.txtConfidence.text = "${(item.confidence * 100).toInt()}%"

            val date = Date(item.timestamp)
            val format = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            binding.txtDate.text = format.format(date)

            if (item.imageUrl.isNotEmpty()) {
                val imgFile = File(item.imageUrl)
                if (imgFile.exists()) {
                    val bitmap = BitmapFactory.decodeFile(imgFile.absolutePath)
                    binding.imgThumbnail.setImageBitmap(bitmap)
                }
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<DiseaseEntity>() {
        override fun areItemsTheSame(oldItem: DiseaseEntity, newItem: DiseaseEntity) = oldItem.id == newItem.id
        override fun areContentsTheSame(oldItem: DiseaseEntity, newItem: DiseaseEntity) = oldItem == newItem
    }
}